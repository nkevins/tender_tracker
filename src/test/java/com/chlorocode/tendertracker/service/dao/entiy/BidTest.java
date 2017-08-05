package com.chlorocode.tendertracker.service.dao.entiy;

import com.chlorocode.tendertracker.dao.entity.Bid;
import com.chlorocode.tendertracker.dao.entity.BidDocument;
import com.chlorocode.tendertracker.dao.entity.BidItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BidTest {

    private Bid bid;

    @Before
    public void setUp() {
        bid = new Bid();
    }

    @Test
    public void testAddBidItem() {
        BidItem item1 = new BidItem();
        item1.setAmount(10);
        bid.addBidItem(item1);

        assertEquals(10, bid.getTotalAmount(), 0.01);
        assertEquals(bid, item1.getBid());
        assertTrue(bid.getBidItems().contains(item1));
        assertEquals(1, bid.getBidItems().size());

        BidItem item2 = new BidItem();
        item2.setAmount(5);
        bid.addBidItem(item2);

        assertEquals(15, bid.getTotalAmount(), 0.01);
        assertEquals(bid, item2.getBid());
        assertTrue(bid.getBidItems().contains(item2));
        assertEquals(2, bid.getBidItems().size());
    }

    @Test
    public void testAddBidDocument() {
        BidDocument doc1 = new BidDocument();
        bid.addBidDocument(doc1);

        assertEquals(bid, doc1.getBid());
        assertTrue(bid.getDocuments().contains(doc1));
        assertEquals(1, bid.getDocuments().size());

        BidDocument doc2 = new BidDocument();
        bid.addBidDocument(doc2);

        assertEquals(bid, doc2.getBid());
        assertTrue(bid.getDocuments().contains(doc2));
        assertEquals(2, bid.getDocuments().size());
    }
}
