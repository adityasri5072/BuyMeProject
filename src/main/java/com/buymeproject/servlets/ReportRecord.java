package com.buymeproject.servlets;

import java.math.BigDecimal;

public class ReportRecord {
    private String identifier;
    private BigDecimal result;
    private String auctionIDs;
    private String winner;

    public ReportRecord(String identifier, BigDecimal result, String auctionIDs, String winner) {
        this.identifier = identifier;
        this.result = result;
        this.auctionIDs = auctionIDs;
        this.winner = winner;
    }

    public String getIdentifier() { return identifier; }
    public BigDecimal getResult() { return result; }
    public String getAuctionIDs() { return auctionIDs; }
    public String getWinner() { return winner; }
}