package com.mediaportal.service.queuehandler2;

import com.mediaportal.commands.core.FactoryMediaInfo;
import com.mediaportal.metadata.strata.MediaInfoMetadata;
import com.mediaportal.service.jpa.MetadataService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static org.junit.Assert.*;

@SpringBootTest
public class FactoryMediaInfoTest {

    File mediaInfoLog1;
    @Before
    public void initXml1(){

        String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MediaInfo>\n" +
                "<media ref=\"/mnt/HIRES/MXP_N3/36718.mxf\">\n" +
                "<track type=\"General\">\n" +
                "<VideoCount>1</VideoCount>\n" +
                "<AudioCount>1</AudioCount>\n" +
                "<OtherCount>3</OtherCount>\n" +
                "<FileExtension>mxf</FileExtension>\n" +
                "<Format>MXF</Format>\n" +
                "<Format_Commercial_IfAny>XDCAM HD 35</Format_Commercial_IfAny>\n" +
                "<Format_Version>1.3</Format_Version>\n" +
                "<Format_Profile>OP-1a</Format_Profile>\n" +
                "<Format_Settings>Closed / Complete</Format_Settings>\n" +
                "<FileSize>7083295095</FileSize>\n" +
                "<Duration>1681.680</Duration>\n" +
                "<OverallBitRate_Mode>VBR</OverallBitRate_Mode>\n" +
                "<OverallBitRate>33696280</OverallBitRate>\n" +
                "<FrameRate>29.970</FrameRate>\n" +
                "<FrameCount>50400</FrameCount>\n" +
                "<FooterSize>6007</FooterSize>\n" +
                "<Encoded_Date>0-00-00 00:00:00.000</Encoded_Date>\n" +
                "<File_Modified_Date>UTC 2022-12-14 22:13:29</File_Modified_Date>\n" +
                "<File_Modified_Date_Local>2022-12-14 19:13:29</File_Modified_Date_Local>\n" +
                "<Encoded_Application_CompanyName>FFmpeg</Encoded_Application_CompanyName>\n" +
                "<Encoded_Application_Name>OP1a Muxer</Encoded_Application_Name>\n" +
                "<Encoded_Application_Version>58.76.100.0.0</Encoded_Application_Version>\n" +
                "<Encoded_Library_Name>Lavf (linux)</Encoded_Library_Name>\n" +
                "<Encoded_Library_Version>58.76.100.0.0</Encoded_Library_Version>\n" +
                "</track>\n" +
                "<track type=\"Video\">\n" +
                "<StreamOrder>0</StreamOrder>\n" +
                "<ID>2</ID>\n" +
                "<Format>MPEG Video</Format>\n" +
                "<Format_Commercial_IfAny>XDCAM HD 35</Format_Commercial_IfAny>\n" +
                "<Format_Version>2</Format_Version>\n" +
                "<Format_Profile>Main</Format_Profile>\n" +
                "<Format_Level>High</Format_Level>\n" +
                "<Format_Settings_BVOP>Yes</Format_Settings_BVOP>\n" +
                "<Format_Settings_Matrix>Default</Format_Settings_Matrix>\n" +
                "<Format_Settings_GOP>M=3, N=15</Format_Settings_GOP>\n" +
                "<Format_Settings_PictureStructure>Frame</Format_Settings_PictureStructure>\n" +
                "<Format_Settings_Wrapping>Frame</Format_Settings_Wrapping>\n" +
                "<CodecID>0D01030102046001-0401020201030300</CodecID>\n" +
                "<Duration>1681.680</Duration>\n" +
                "<BitRate_Mode>VBR</BitRate_Mode>\n" +
                "<BitRate>0</BitRate>\n" +
                "<BitRate_Maximum>35000000</BitRate_Maximum>\n" +
                "<Width>1920</Width>\n" +
                "<Height>1080</Height>\n" +
                "<Sampled_Width>1920</Sampled_Width>\n" +
                "<Sampled_Height>1080</Sampled_Height>\n" +
                "<PixelAspectRatio>1.000</PixelAspectRatio>\n" +
                "<DisplayAspectRatio>1.778</DisplayAspectRatio>\n" +
                "<FrameRate>29.970</FrameRate>\n" +
                "<FrameCount>50400</FrameCount>\n" +
                "<Standard>NTSC</Standard>\n" +
                "<ColorSpace>YUV</ColorSpace>\n" +
                "<ChromaSubsampling>4:2:0</ChromaSubsampling>\n" +
                "<BitDepth>8</BitDepth>\n" +
                "<ScanType>Interlaced</ScanType>\n" +
                "<ScanOrder>TFF</ScanOrder>\n" +
                "<Compression_Mode>Lossy</Compression_Mode>\n" +
                "<Delay>55499.911</Delay>\n" +
                "<Delay_Original>3937.500</Delay_Original>\n" +
                "<TimeCode_FirstFrame>01:05:37:15</TimeCode_FirstFrame>\n" +
                "<TimeCode_Source>Group of pictures header</TimeCode_Source>\n" +
                "<Gop_OpenClosed>Open</Gop_OpenClosed>\n" +
                "<BufferSize>1222656</BufferSize>\n" +
                "<colour_description_present>Yes</colour_description_present>\n" +
                "<colour_description_present_Source>Stream</colour_description_present_Source>\n" +
                "<colour_range>Limited</colour_range>\n" +
                "<colour_range_Source>Container</colour_range_Source>\n" +
                "<colour_primaries>BT.709</colour_primaries>\n" +
                "<colour_primaries_Source>Container / Stream</colour_primaries_Source>\n" +
                "<transfer_characteristics>BT.709</transfer_characteristics>\n" +
                "<transfer_characteristics_Source>Container / Stream</transfer_characteristics_Source>\n" +
                "<matrix_coefficients>BT.709</matrix_coefficients>\n" +
                "<matrix_coefficients_Source>Container / Stream</matrix_coefficients_Source>\n" +
                "<extra>\n" +
                "<Delay_SDTI>55499911</Delay_SDTI>\n" +
                "<intra_dc_precision>10</intra_dc_precision>\n" +
                "</extra>\n" +
                "</track>\n" +
                "<track type=\"Audio\">\n" +
                "<StreamOrder>1</StreamOrder>\n" +
                "<ID>3</ID>\n" +
                "<Format>PCM</Format>\n" +
                "<Format_Settings_Endianness>Little</Format_Settings_Endianness>\n" +
                "<Format_Settings_Wrapping>Frame (AES)</Format_Settings_Wrapping>\n" +
                "<CodecID>0D01030102060300</CodecID>\n" +
                "<Duration>1681.680</Duration>\n" +
                "<BitRate_Mode>CBR</BitRate_Mode>\n" +
                "<BitRate>2304000</BitRate>\n" +
                "<Channels>2</Channels>\n" +
                "<SamplesPerFrame>1601.6</SamplesPerFrame>\n" +
                "<SamplingRate>48000</SamplingRate>\n" +
                "<SamplingCount>80720640</SamplingCount>\n" +
                "<FrameRate>29.970</FrameRate>\n" +
                "<FrameCount>50400</FrameCount>\n" +
                "<BitDepth>24</BitDepth>\n" +
                "<Delay>55499.911</Delay>\n" +
                "<Delay_DropFrame>Yes</Delay_DropFrame>\n" +
                "<Delay_Source>Container</Delay_Source>\n" +
                "<StreamSize>484323840</StreamSize>\n" +
                "<StreamSize_Proportion>0.06838</StreamSize_Proportion>\n" +
                "<extra>\n" +
                "<Delay_SDTI>55499911</Delay_SDTI>\n" +
                "<Locked>Yes</Locked>\n" +
                "<BlockAlignment>6</BlockAlignment>\n" +
                "</extra>\n" +
                "</track>\n" +
                "<track type=\"Other\" typeorder=\"1\">\n" +
                "<ID>1-Material</ID>\n" +
                "<Type>Time code</Type>\n" +
                "<Format>MXF TC</Format>\n" +
                "<FrameRate>29.970</FrameRate>\n" +
                "<TimeCode_FirstFrame>15:24:59;28</TimeCode_FirstFrame>\n" +
                "<TimeCode_Settings>Material Package</TimeCode_Settings>\n" +
                "<TimeCode_Striped>Yes</TimeCode_Striped>\n" +
                "</track>\n" +
                "<track type=\"Other\" typeorder=\"2\">\n" +
                "<ID>1-Source</ID>\n" +
                "<Type>Time code</Type>\n" +
                "<Format>MXF TC</Format>\n" +
                "<FrameRate>29.970</FrameRate>\n" +
                "<TimeCode_FirstFrame>15:24:59;28</TimeCode_FirstFrame>\n" +
                "<TimeCode_Settings>Source Package</TimeCode_Settings>\n" +
                "<TimeCode_Striped>Yes</TimeCode_Striped>\n" +
                "</track>\n" +
                "<track type=\"Other\" typeorder=\"3\">\n" +
                "<Type>Time code</Type>\n" +
                "<Format>SMPTE TC</Format>\n" +
                "<MuxingMode>SDTI</MuxingMode>\n" +
                "<FrameRate>29.970</FrameRate>\n" +
                "<TimeCode_FirstFrame>15:24:59;28</TimeCode_FirstFrame>\n" +
                "</track>\n" +
                "</media>\n" +
                "</MediaInfo>";

        try {
            mediaInfoLog1 = File.createTempFile("mock", ".xml");
            // Grava o conteúdo XML no arquivo
            FileWriter writer = new FileWriter(mediaInfoLog1);
            writer.write(xml1);
            writer.close();

            // Utilize o arquivo temporário nos seus testes
            System.out.println("Arquivo mock criado: " + mediaInfoLog1.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registraParametros() throws Exception {
        MetadataService metadataService = Mockito.mock(MetadataService.class);
        FactoryMediaInfo mediaInfo = new FactoryMediaInfo();
        mediaInfo.setMetadataMgr(metadataService);
        int assetId = 145;
        mediaInfo.assetId = 145;
        MediaInfoMetadata mediaInfoMetadata = new MediaInfoMetadata("teste.mp4", MediaInfoMetadata.Qualifiers.MEDIAINFO_GENERAL, "teste");
        Mockito.when(metadataService.create(assetId, mediaInfoMetadata)).thenReturn(100L);
        mediaInfo.registraParametros(mediaInfoLog1.getAbsolutePath(), 1055);
        mediaInfoLog1.delete();
        Map<String,String> dummyParams = mediaInfo.getDummyPrameters();
        assertEquals(dummyParams.get("FORMATO"), "mxf");
        assertTrue(validarFormato(dummyParams.get("DURATION")));

    }

    @Test
    public void duration(){
        FactoryMediaInfo mediaInfo = new FactoryMediaInfo();
        assertEquals("08:33:22:00", mediaInfo.parseDuration("08h 33min 22s").getHhmmssff());
        assertEquals("00:28:01:20", mediaInfo.parseDuration("1681.680").getHhmmssff());
    }

    private boolean validarFormato(String str) {
        String formato = "HH:mm:ss:SS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);

        try {
            formatter.parse(str);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}