package com.omniverstech.stockapp;

import org.springframework.boot.SpringApplication;

public class TestStockappApplication {

	public static void main(String[] args) {
		SpringApplication.from(StockappApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
