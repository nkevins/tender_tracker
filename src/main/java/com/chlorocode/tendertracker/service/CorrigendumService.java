package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Corrigendum;
import com.chlorocode.tendertracker.dao.entity.CorrigendumDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for corrigendum
 */
public interface CorrigendumService {

    /**
     * This method is used to find corrigendum by id.
     *
     * @param corrigendumId unique identifier of the corrigendum
     * @return Corrigendum
     */
    Corrigendum findCorrigendumById(int corrigendumId);

    /**
     * This method is used to find all corrigendums for a particular tender.
     *
     * @param tenderId unique identifier of the tender
     * @return list of corrigendum
     */
    List<Corrigendum> findTenderCorrigendum(int tenderId);

    /**
     * This method is used to add new corrigendum.
     *
     * @param corrigendum corrigendum object to be added
     * @param attachments list of files to be attached with the corrigendum
     * @return Corrigendum
     */
    Corrigendum addCorrigendum(Corrigendum corrigendum, List<MultipartFile> attachments);

    /**
     * This method is used to update a corrigendum.
     *
     * @param corrigendum corrigendum object to be uodated
     * @return Corrigendum
     */
    Corrigendum updateCorrigendum(Corrigendum corrigendum);

    /**
     * This method is used to remove a corrigendum.
     *
     * @param corrigendumId unique identifier of the corrigendum
     */
    void removeCorrigendum(int corrigendumId);

    /**
     * This method is used to add a new document to a corrigendum.
     *
     * @param attachment attachment to be added
     * @param corrigendum corrigendum object
     * @param createdBy user who add the document
     * @return CorrigendumDocument
     */
    CorrigendumDocument addCorrigendumDocument(MultipartFile attachment, Corrigendum corrigendum, int createdBy);

    /**
     * This method is used to remove document form a corrigendum.
     *
     * @param id unique identifier of the document
     */
    void removeCorrigendumDocument(int id);
}
