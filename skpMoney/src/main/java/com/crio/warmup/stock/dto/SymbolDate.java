package com.crio.warmup.stock.dto;

public class SymbolDate {
    private String symbol;
    private String purchaseDate;
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    public SymbolDate(String symbol, String purchaseDate) {
        this.symbol = symbol;
        this.purchaseDate = purchaseDate;
    }
}