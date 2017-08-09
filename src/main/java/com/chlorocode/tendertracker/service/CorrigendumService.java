package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Corrigendum;
import com.chlorocode.tendertracker.dao.entity.CorrigendumDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CorrigendumService {

    Corrigendum findCorrigendumById(int corrigendumId);
    List<Corrigendum> findTenderCorrigendum(int tenderId);
    Corrigendum addCorrigendum(Corrigendum corrigendum, List<MultipartFile> attachments);
    Corrigendum updateCorrigendum(Corrigendum corrigendum);
    void removeCorrigendum(int corrigendumId);
    CorrigendumDocument addCorrigendumDocument(MultipartFile attachment, Corrigendum corrigendum, int createdBy);
    void removeCorrigendumDocument(int id);
}
