package com.mediaportal.service.plugins.ingest;

import com.mediaportal.jpa.domain.McAssetType;
import com.mediaportal.jpa.domain.MxMetaview;
import com.mediaportal.metadata.Metadata;
import com.mediaportal.service.jpa.*;
import org.junit.Test;
import java.util.HashMap;
import static org.mockito.Mockito.*;

public class AutoIngestPluginByAssetTypeTest {

    /**
     * If it doest generate exception its ok!!!!
     */
    @Test(expected = RuntimeException.class)
    public void ingest() {
        McAssetType videoType = new McAssetType();
        MxMetaviewService mxMetaviewService = mock(MxMetaviewService.class);
        MxMetaview videoMtw = new MxMetaview();
        videoMtw.setMetaviewid("433");
        videoMtw.setAssettypeid("10");
        videoMtw.setPrimarytable("tvsd_video");
        MetaviewAssetService mtwService = mock(MetaviewAssetService.class);

        McAssetMasterService mcAssetMasterService = mock(McAssetMasterService.class);

        videoType.setMetaviewid(433);
        videoType.setAssettypeid(10);

        AutoIngestPluginByAssetType auto = new AutoIngestPluginByAssetType(null);
        McAssetTypeService assetTypeService = mock(McAssetTypeService.class);
        auto.setAssetTypeService(assetTypeService);
        auto.setAssetTypeId(10);
        auto.setMcAssetMasterService(mcAssetMasterService);
        auto.setMxMetaviewService(mxMetaviewService);
        auto.setMetaviewService(mtwService);

        when(assetTypeService.findById(10)).thenReturn(videoType);
        when(mxMetaviewService.listById("433")).thenReturn(videoMtw);
        doNothing().when(mtwService).create(anyInt(), anyInt(), anyMap(), anyString());
        when(mcAssetMasterService.mxCreateAsset(anyString(), anyInt(), anyInt(), anyString())).thenReturn(new HashMap<String, Integer>() {{
            put("assetid", 1);
        }});
        auto.ingest("teste", "teste", null, 1010, new HashMap<String, Metadata>());

        when(assetTypeService.findById(anyInt())).thenReturn(null);

        auto.ingest("teste", "teste", null, 1010, new HashMap<String, Metadata>());
    }

}