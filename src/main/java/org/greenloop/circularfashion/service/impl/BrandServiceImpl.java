package org.greenloop.circularfashion.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.Brand;
import org.greenloop.circularfashion.entity.request.BrandCreateRequest;
import org.greenloop.circularfashion.entity.request.BrandUpdateRequest;
import org.greenloop.circularfashion.entity.response.BrandResponse;
import org.greenloop.circularfashion.exception.ResourceNotFoundException;
import org.greenloop.circularfashion.mapper.BrandMapper;
import org.greenloop.circularfashion.repository.BrandRepository;
import org.greenloop.circularfashion.repository.ItemRepository;
import org.greenloop.circularfashion.service.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ItemRepository itemRepository;
    private final BrandMapper brandMapper;

    @Override
    public BrandResponse createBrand(BrandCreateRequest request) {
        log.info("Creating brand: {}", request.getName());
        
        // Check if brand with same name exists
        if (brandRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Brand with name '" + request.getName() + "' already exists");
        }
        
        Brand brand = brandMapper.toEntity(request);
        Brand savedBrand = brandRepository.save(brand);
        
        log.info("Brand created successfully with id: {}", savedBrand.getBrandId());
        return brandMapper.toResponse(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(UUID id) {
        log.debug("Fetching brand by id: {}", id);
        Brand brand = getEntityById(id);
        Long itemCount = (long) itemRepository.findByBrandId(id).size();
        return brandMapper.toResponse(brand, itemCount);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandBySlug(String slug) {
        log.debug("Fetching brand by slug: {}", slug);
        Brand brand = getEntityBySlug(slug);
        Long itemCount = (long) itemRepository.findByBrandId(brand.getBrandId()).size();
        return brandMapper.toResponse(brand, itemCount);
    }

    @Override
    public BrandResponse updateBrand(UUID id, BrandUpdateRequest request) {
        log.info("Updating brand: {}", id);
        
        Brand brand = getEntityById(id);
        
        // Check if new name conflicts with existing brand
        if (request.getName() != null && !request.getName().equals(brand.getName())) {
            if (brandRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Brand with name '" + request.getName() + "' already exists");
            }
        }
        
        brandMapper.updateEntity(brand, request);
        Brand updatedBrand = brandRepository.save(brand);
        
        log.info("Brand updated successfully: {}", id);
        return brandMapper.toResponse(updatedBrand);
    }

    @Override
    public void deleteBrand(UUID id) {
        log.info("Soft deleting brand: {}", id);
        
        Brand brand = getEntityById(id);
        
        // Soft delete: Set isActive to false instead of removing from database
        brand.setIsActive(false);
        brandRepository.save(brand);
        
        log.info("Brand soft deleted successfully (set to inactive): {}", id);
    }

    @Override
    public void hardDeleteBrand(UUID id) {
        log.info("Hard deleting brand: {}", id);
        
        Brand brand = getEntityById(id);
        
        // Check if brand has items - prevent hard delete if so
        List<?> items = itemRepository.findByBrandId(id);
        if (!items.isEmpty()) {
            throw new IllegalStateException("Cannot permanently delete brand with " + items.size() + " items. Please reassign or delete items first.");
        }
        
        brandRepository.delete(brand);
        log.info("Brand permanently deleted: {}", id);
    }

    @Override
    public BrandResponse restoreBrand(UUID id) {
        log.info("Restoring soft-deleted brand: {}", id);
        
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        
        brand.setIsActive(true);
        Brand restoredBrand = brandRepository.save(brand);
        
        log.info("Brand restored successfully: {}", id);
        return brandMapper.toResponse(restoredBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public Brand getEntityById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Brand getEntityBySlug(String slug) {
        return brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "slug", slug));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        log.debug("Fetching all brands with pagination");
        Page<Brand> brands = brandRepository.findAll(pageable);
        return brands.map(brandMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllActiveBrands() {
        log.debug("Fetching all active brands");
        List<Brand> brands = brandRepository.findByIsActiveTrue();
        return brandMapper.toResponseList(brands);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllInactiveBrands() {
        log.debug("Fetching all inactive (soft-deleted) brands");
        List<Brand> brands = brandRepository.findByIsActiveFalse();
        return brandMapper.toResponseList(brands);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> searchBrands(String keyword, Pageable pageable) {
        log.debug("Searching brands with keyword: {}", keyword);
        Page<Brand> brands = brandRepository.searchBrands(keyword, pageable);
        return brands.map(brandMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getVerifiedBrands() {
        log.debug("Fetching verified brands");
        List<Brand> brands = brandRepository.findByIsVerifiedTrue();
        return brandMapper.toResponseList(brands);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getPartnerBrands() {
        log.debug("Fetching partner brands");
        List<Brand> brands = brandRepository.findByIsPartnerTrue();
        return brandMapper.toResponseList(brands);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getSustainableBrands() {
        log.debug("Fetching sustainable brands");
        List<Brand> brands = brandRepository.findBySustainabilityRatingGreaterThanEqual(BigDecimal.valueOf(3.0));
        return brandMapper.toResponseList(brands);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countBrands() {
        return brandRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countVerifiedBrands() {
        return brandRepository.countByIsVerifiedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPartnerBrands() {
        return brandRepository.countByIsPartnerTrue();
    }
}

