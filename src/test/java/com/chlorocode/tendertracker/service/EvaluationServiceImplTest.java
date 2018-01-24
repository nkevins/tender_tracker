package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.EvaluationResultDAO;
import com.chlorocode.tendertracker.dao.entity.EvaluationResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationServiceImplTest {

    @Mock
    EvaluationResultDAO evaluationResultDAO;

    @InjectMocks
    EvaluationServiceImpl evaluationService;

    @Test
    public void testDuplicateEvaluationCheck() {
        List<EvaluationResult> mockReturn = new LinkedList<>();
        EvaluationResult res = new EvaluationResult();
        mockReturn.add(res);

        when(evaluationResultDAO.findEvaluationResultByBidAndEvaluator(1, 1)).thenReturn(mockReturn);

        boolean result = evaluationService.isDuplicateEvaluation(1, 1);
        assertTrue(result);
    }

    @Test
    public void testNoDuplicateEvaluationCheck() {
        List<EvaluationResult> mockReturn = new LinkedList<>();

        when(evaluationResultDAO.findEvaluationResultByBidAndEvaluator(1, 1)).thenReturn(mockReturn);

        boolean result = evaluationService.isDuplicateEvaluation(1, 1);
        assertFalse(result);
    }
}
