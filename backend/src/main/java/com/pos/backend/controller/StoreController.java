package com.pos.backend.controller;

import com.pos.backend.domain.Store;
import com.pos.backend.dto.StoreDTO;
import com.pos.backend.mapper.StoreMapper;
import com.pos.backend.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Store operations.
 * Provides endpoints for CRUD operations on stores.
 * 
 * @author Point of Sale System
 * @version 2.0 (Modernized)
 */
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stores", description = "Store management APIs")
public class StoreController {
    
    private final StoreService storeService;
    private final StoreMapper storeMapper;
    
    @Operation(summary = "Get all stores", description = "Retrieve a list of all stores")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping
    public ResponseEntity<List<StoreDTO.Response>> getAllStores() {
        log.debug("GET /api/stores - Get all stores");
        List<StoreDTO.Response> stores = storeService.findAll().stream()
                .map(storeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }
    
    @Operation(summary = "Get store by ID", description = "Retrieve a specific store by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved store"),
        @ApiResponse(responseCode = "404", description = "Store not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO.Response> getStoreById(
            @Parameter(description = "Store ID") @PathVariable Long id) {
        log.debug("GET /api/stores/{} - Get store by id", id);
        return storeService.findById(id)
                .map(storeMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Get store by number", description = "Retrieve a store by its store number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved store"),
        @ApiResponse(responseCode = "404", description = "Store not found")
    })
    @GetMapping("/number/{number}")
    public ResponseEntity<StoreDTO.Response> getStoreByNumber(
            @Parameter(description = "Store number") @PathVariable String number) {
        log.debug("GET /api/stores/number/{} - Get store by number", number);
        return storeService.findByNumber(number)
                .map(storeMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Search stores by name", description = "Search stores by name keyword")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    @GetMapping("/search")
    public ResponseEntity<List<StoreDTO.Response>> searchStores(
            @Parameter(description = "Search keyword") @RequestParam String name) {
        log.debug("GET /api/stores/search?name={} - Search stores", name);
        List<StoreDTO.Response> results = storeService.searchByName(name).stream()
                .map(storeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
    
    @Operation(summary = "Get stores by city", description = "Retrieve all stores in a specific city")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stores")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<StoreDTO.Response>> getStoresByCity(
            @Parameter(description = "City name") @PathVariable String city) {
        log.debug("GET /api/stores/city/{} - Get stores by city", city);
        List<StoreDTO.Response> stores = storeService.findByCity(city).stream()
                .map(storeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }
    
    @Operation(summary = "Get stores by state", description = "Retrieve all stores in a specific state")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stores")
    @GetMapping("/state/{state}")
    public ResponseEntity<List<StoreDTO.Response>> getStoresByState(
            @Parameter(description = "State code (2 letters)") @PathVariable String state) {
        log.debug("GET /api/stores/state/{} - Get stores by state", state);
        List<StoreDTO.Response> stores = storeService.findByState(state).stream()
                .map(storeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }
    
    @Operation(summary = "Get stores by location", description = "Retrieve stores by city and state")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stores")
    @GetMapping("/location")
    public ResponseEntity<List<StoreDTO.Response>> getStoresByLocation(
            @Parameter(description = "City name") @RequestParam String city,
            @Parameter(description = "State code") @RequestParam String state) {
        log.debug("GET /api/stores/location?city={}&state={} - Get stores by location", city, state);
        List<StoreDTO.Response> stores = storeService.findByCityAndState(city, state).stream()
                .map(storeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }
    
    @Operation(summary = "Get active stores", description = "Retrieve all active stores")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active stores")
    @GetMapping("/active")
    public ResponseEntity<List<StoreDTO.Response>> getActiveStores() {
        log.debug("GET /api/stores/active - Get active stores");
        List<StoreDTO.Response> stores = storeService.findAllActive().stream()
                .map(storeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }
    
    @Operation(summary = "Get stores by manager", description = "Retrieve stores managed by a specific person")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stores")
    @GetMapping("/manager/{manager}")
    public ResponseEntity<List<StoreDTO.Response>> getStoresByManager(
            @Parameter(description = "Manager name") @PathVariable String manager) {
        log.debug("GET /api/stores/manager/{} - Get stores by manager", manager);
        List<StoreDTO.Response> stores = storeService.findByManager(manager).stream()
                .map(storeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }
    
    @Operation(summary = "Create store", description = "Create a new store")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Store created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate store number")
    })
    @PostMapping
    public ResponseEntity<?> createStore(@Valid @RequestBody StoreDTO.Request request) {
        log.debug("POST /api/stores - Create store: {}", request.getNumber());
        Store store = storeMapper.toEntity(request);
        Store created = storeService.create(store);
        return ResponseEntity.status(HttpStatus.CREATED).body(storeMapper.toResponse(created));
    }
    
    @Operation(summary = "Update store", description = "Update an existing store")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Store updated successfully"),
        @ApiResponse(responseCode = "404", description = "Store not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStore(
            @Parameter(description = "Store ID") @PathVariable Long id,
            @Valid @RequestBody StoreDTO.Request request) {
        log.debug("PUT /api/stores/{} - Update store", id);
        Store store = storeMapper.toEntity(request);
        Store updated = storeService.update(id, store);
        return ResponseEntity.ok(storeMapper.toResponse(updated));
    }
    
    @Operation(summary = "Delete store", description = "Delete a store by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Store deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Store not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStore(
            @Parameter(description = "Store ID") @PathVariable Long id) {
        log.debug("DELETE /api/stores/{} - Delete store", id);
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Deactivate store", description = "Deactivate a store (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Store deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Store not found")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateStore(
            @Parameter(description = "Store ID") @PathVariable Long id) {
        log.debug("PATCH /api/stores/{}/deactivate - Deactivate store", id);
        Store deactivated = storeService.deactivate(id);
        return ResponseEntity.ok(storeMapper.toResponse(deactivated));
    }
    
    @Operation(summary = "Activate store", description = "Activate a previously deactivated store")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Store activated successfully"),
        @ApiResponse(responseCode = "404", description = "Store not found")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateStore(
            @Parameter(description = "Store ID") @PathVariable Long id) {
        log.debug("PATCH /api/stores/{}/activate - Activate store", id);
        Store activated = storeService.activate(id);
        return ResponseEntity.ok(storeMapper.toResponse(activated));
    }
    
    @Operation(summary = "Count stores", description = "Get the total count of stores")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count")
    public ResponseEntity<Long> countStores() {
        log.debug("GET /api/stores/count - Count stores");
        long count = storeService.count();
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Count active stores", description = "Get the count of active stores")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveStores() {
        log.debug("GET /api/stores/count/active - Count active stores");
        long count = storeService.countActive();
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Count stores by state", description = "Get the count of stores in a specific state")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
    @GetMapping("/count/state/{state}")
    public ResponseEntity<Long> countStoresByState(
            @Parameter(description = "State code") @PathVariable String state) {
        log.debug("GET /api/stores/count/state/{} - Count stores by state", state);
        long count = storeService.countByState(state);
        return ResponseEntity.ok(count);
    }
}
