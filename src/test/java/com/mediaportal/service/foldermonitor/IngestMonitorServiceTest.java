package com.mediaportal.service.foldermonitor;

import com.mediaportal.jpa.domain.CaFluxo;
import com.mediaportal.metadata.Metadata;
import com.mediaportal.metadata.StringMetadata;
import com.mediaportal.service.jpa.CaFluxoService;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IngestMonitorServiceTest {

    @Test
    public void extractFlowByMetadataMap() {
        CaFluxo initCaFluxo = new CaFluxo("JustAnotherFlow");
        CaFluxoService caFluxoServiceMock = mock(CaFluxoService.class);
        StringMetadata flowName = new StringMetadata("FlowTeste", StringMetadata.Qualifiers.FLOWNAME);

        Map<String, Metadata> metadataMap = new HashMap<>();
        Map<String, Metadata> nullMetadataMap = new HashMap<>();
        metadataMap.put("FLOWNAME", flowName);

        IngestMonitorService ims = new IngestMonitorService();
        ims.setCaFluxoService(caFluxoServiceMock);
        when(caFluxoServiceMock.getByName("FlowTeste")).thenReturn(new CaFluxo("FlowTeste"));

        CaFluxo fileFlow = ims.extractFlowByMetadataMap(metadataMap, initCaFluxo);
        CaFluxo fileFlowNull = ims.extractFlowByMetadataMap(nullMetadataMap, initCaFluxo);
        assertEquals("FlowTeste", fileFlow.getName());
        assertEquals("JustAnotherFlow", fileFlowNull.getName());
    }
}