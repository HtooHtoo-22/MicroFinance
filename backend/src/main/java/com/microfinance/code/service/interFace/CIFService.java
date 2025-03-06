package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CIFService {
     CIFDTO createCIF(CIFDTO cifDTO, MultipartFile frontNRC, MultipartFile backNRC, MultipartFile userPhoto, User user) throws IOException;
     List<CIFDTO> getAllCIFs();
     CIFDTO updateCIF(Integer id, Map<String, Object> updates);
     CIFDTO updateCIFStatus(Integer id, String status);
     List<CIFDTO> getActiveCIFs();
     List<CIFDTO> getDeleteCIFs();

     CIFDTO getCifById(Integer id);
}
