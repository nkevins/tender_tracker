package com.chlorocode.tendertracker.service.dao.entiy;

import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderDocument;
import com.chlorocode.tendertracker.dao.entity.TenderItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TenderTest {

    private Tender tender;

    @Before
    public void setUp() {
        tender = new Tender();
    }

    @Test
    public void testAddTenderItem() {
        TenderItem item1 = new TenderItem();
        tender.addTenderItem(item1);

        assertEquals(tender, item1.getTender());
        assertTrue(tender.getItems().contains(item1));
        assertEquals(1, tender.getItems().size());

        TenderItem item2 = new TenderItem();
        tender.addTenderItem(item2);

        assertEquals(tender, item2.getTender());
        assertTrue(tender.getItems().contains(item2));
        assertEquals(2, tender.getItems().size());
    }

    @Test
    public void testAddTenderDocument() {
        TenderDocument doc1 = new TenderDocument();
        tender.addTenderDocument(doc1);

        assertEquals(tender, doc1.getTender());
        assertTrue(tender.getDocuments().contains(doc1));
        assertEquals(1, tender.getDocuments().size());

        TenderDocument doc2 = new TenderDocument();
        tender.addTenderDocument(doc2);

        assertEquals(tender, doc2.getTender());
        assertTrue(tender.getDocuments().contains(doc2));
        assertEquals(2, tender.getDocuments().size());
    }
}
