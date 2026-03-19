package com.pos.backend.config;

import com.pos.backend.domain.*;
import com.pos.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeds the database with sample data on application startup.
 * Only runs when the "dev" profile is active or no profile is set (default).
 * Uses data inspired by the original StoreData.csv.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!prod")
public class DataSeeder implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final PersonRepository personRepository;
    private final CashierRepository cashierRepository;
    private final TaxCategoryRepository taxCategoryRepository;
    private final TaxRateRepository taxRateRepository;
    private final ItemRepository itemRepository;
    private final RegisterRepository registerRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (storeRepository.count() > 0) {
            log.info("Database already contains data — skipping seed.");
            return;
        }

        log.info("Seeding database with sample data...");

        // ── Store ────────────────────────────────────────────
        Store store = new Store();
        store.setNumber("S001");
        store.setName("David's Quick Mart");
        store.setAddress("100 Main Street");
        store.setCity("Edmond");
        store.setState("OK");
        store.setZip("73034");
        store.setPhone("405-348-0001");
        store.setEmail("info@davidsquickmart.com");
        store.setManager("David Martin");
        store.setIsActive(true);
        store.setOpenedDate(LocalDateTime.of(2023, 1, 1, 8, 0));
        store = storeRepository.save(store);

        // ── Persons ──────────────────────────────────────────
        Person david = personRepository.save(
                new Person("David", "Martin", "1 1st Street", "Edmond", "OK", "73034", "405-348-1111", "111-11-1111"));
        Person sally = personRepository.save(
                new Person("Sally", "Jones", "2 2nd Street", "Edmond", "OK", "73034", "405-348-2222", "222-22-2222"));
        Person mike = personRepository.save(
                new Person("Mike", "Brown", "3 3rd Street", "Edmond", "OK", "73034", "405-348-3333", "333-33-3333"));

        // ── Cashiers ─────────────────────────────────────────
        Cashier cashier1 = new Cashier();
        cashier1.setNumber("C001");
        cashier1.setPassword("password1");
        cashier1.setPerson(david);
        cashier1.setStore(store);
        cashier1.setIsActive(true);
        cashier1.setHireDate(LocalDateTime.of(2023, 1, 15, 9, 0));
        cashier1.setRole("Supervisor");
        cashier1 = cashierRepository.save(cashier1);

        Cashier cashier2 = new Cashier();
        cashier2.setNumber("C002");
        cashier2.setPassword("password2");
        cashier2.setPerson(sally);
        cashier2.setStore(store);
        cashier2.setIsActive(true);
        cashier2.setHireDate(LocalDateTime.of(2023, 2, 1, 9, 0));
        cashier2.setRole("Cashier");
        cashier2 = cashierRepository.save(cashier2);

        Cashier cashier3 = new Cashier();
        cashier3.setNumber("C003");
        cashier3.setPassword("password3");
        cashier3.setPerson(mike);
        cashier3.setStore(store);
        cashier3.setIsActive(true);
        cashier3.setHireDate(LocalDateTime.of(2023, 6, 1, 9, 0));
        cashier3.setRole("Cashier");
        cashierRepository.save(cashier3);

        // ── Tax Categories & Rates ──────────────────────────
        TaxCategory food = new TaxCategory("Food", "Prepared and packaged food items");
        food.setIsActive(true);
        food = taxCategoryRepository.save(food);

        TaxCategory beverage = new TaxCategory("Beverage", "Non-alcoholic beverages");
        beverage.setIsActive(true);
        beverage = taxCategoryRepository.save(beverage);

        TaxCategory generalMerchandise = new TaxCategory("General Merchandise", "Non-food taxable goods");
        generalMerchandise.setIsActive(true);
        generalMerchandise = taxCategoryRepository.save(generalMerchandise);

        TaxCategory alcohol = new TaxCategory("Alcohol", "Beer, wine and spirits");
        alcohol.setIsActive(true);
        alcohol = taxCategoryRepository.save(alcohol);

        taxRateRepository.save(new TaxRate(new BigDecimal("0.0700"), LocalDate.of(2023, 1, 1), food));
        taxRateRepository.save(new TaxRate(new BigDecimal("0.0000"), LocalDate.of(2023, 1, 1), beverage));
        taxRateRepository.save(new TaxRate(new BigDecimal("0.0850"), LocalDate.of(2023, 1, 1), generalMerchandise));
        taxRateRepository.save(new TaxRate(new BigDecimal("0.1300"), LocalDate.of(2023, 1, 1), alcohol));

        // ── Registers ────────────────────────────────────────
        Register reg1 = new Register("R001", store);
        reg1.setDescription("Front Register - Lane 1");
        reg1.setIsActive(true);
        reg1.setStatus("CLOSED");
        reg1.setInstalledDate(LocalDateTime.of(2023, 1, 1, 8, 0));
        registerRepository.save(reg1);

        Register reg2 = new Register("R002", store);
        reg2.setDescription("Front Register - Lane 2");
        reg2.setIsActive(true);
        reg2.setStatus("CLOSED");
        reg2.setInstalledDate(LocalDateTime.of(2023, 1, 1, 8, 0));
        registerRepository.save(reg2);

        Register reg3 = new Register("R003", store);
        reg3.setDescription("Self-Checkout Kiosk");
        reg3.setIsActive(true);
        reg3.setStatus("CLOSED");
        reg3.setInstalledDate(LocalDateTime.of(2024, 3, 15, 8, 0));
        registerRepository.save(reg3);

        // ── Items ────────────────────────────────────────────
        createItem("1001", "Turkey Sandwich", new BigDecimal("2.59"), new BigDecimal("1.25"),
                50, 10, 100, "11111111111", "SKU-1001", "Fresh Foods", "Sandwiches",
                food, store);
        createItem("1002", "Ham Sandwich", new BigDecimal("2.59"), new BigDecimal("1.20"),
                45, 10, 100, "22222222222", "SKU-1002", "Fresh Foods", "Sandwiches",
                food, store);
        createItem("1003", "Coca-Cola 20oz", new BigDecimal("0.97"), new BigDecimal("0.45"),
                120, 24, 200, "33333333333", "SKU-1003", "Coca-Cola", "Beverages",
                beverage, store);
        createItem("1004", "Dr. Pepper 20oz", new BigDecimal("0.97"), new BigDecimal("0.45"),
                100, 24, 200, "44444444444", "SKU-1004", "Dr Pepper", "Beverages",
                beverage, store);
        createItem("1005", "Bottled Water 16oz", new BigDecimal("0.79"), new BigDecimal("0.20"),
                200, 48, 300, "55555555555", "SKU-1005", "Dasani", "Beverages",
                beverage, store);
        createItem("1006", "Chips - BBQ", new BigDecimal("1.49"), new BigDecimal("0.65"),
                75, 20, 150, "66666666666", "SKU-1006", "Lay's", "Snacks",
                food, store);
        createItem("1007", "Candy Bar", new BigDecimal("1.29"), new BigDecimal("0.55"),
                90, 20, 150, "77777777777", "SKU-1007", "Snickers", "Snacks",
                food, store);
        createItem("1008", "Notebook", new BigDecimal("3.99"), new BigDecimal("1.50"),
                30, 5, 50, "88888888888", "SKU-1008", "Mead", "Office Supplies",
                generalMerchandise, store);
        createItem("1009", "Pen Pack (3)", new BigDecimal("2.49"), new BigDecimal("0.80"),
                40, 10, 60, "99999999999", "SKU-1009", "BIC", "Office Supplies",
                generalMerchandise, store);
        createItem("1010", "Energy Drink", new BigDecimal("2.99"), new BigDecimal("1.10"),
                60, 12, 100, "10101010101", "SKU-1010", "Red Bull", "Beverages",
                beverage, store);

        log.info("Database seeded successfully: 1 store, {} persons, {} cashiers, {} tax categories, {} items, {} registers.",
                personRepository.count(), cashierRepository.count(),
                taxCategoryRepository.count(), itemRepository.count(),
                registerRepository.count());
    }

    private void createItem(String number, String description, BigDecimal price, BigDecimal cost,
                            int qty, int minQty, int maxQty, String barcode, String sku,
                            String brand, String category, TaxCategory taxCategory, Store store) {
        Item item = new Item();
        item.setNumber(number);
        item.setDescription(description);
        item.setPrice(price);
        item.setCost(cost);
        item.setQuantity(qty);
        item.setMinQuantity(minQty);
        item.setMaxQuantity(maxQty);
        item.setBarcode(barcode);
        item.setSku(sku);
        item.setBrand(brand);
        item.setCategory(category);
        item.setTaxCategory(taxCategory);
        item.setStore(store);
        item.setIsActive(true);
        item.setIsTaxable(true);
        item.setCreatedDate(LocalDateTime.now());
        itemRepository.save(item);
    }
}
