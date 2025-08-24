package com.ps.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.ps.constants.IValidationConstants;
import com.ps.dto.RelationDTO;
import com.ps.dto.SubProfileDTO;
import com.ps.entity.SubProfile;
import com.ps.exception.ProfileException;

public interface ProfileUtil {

	public static boolean isSupportedImageContentType(String contentType) {
		return contentType.equals("image/jpeg") ||
				contentType.equals("image/png")  ||
				contentType.equals("image/jpg");
	}
	
	public static SubProfileDTO prepareSubProfileDTO(SubProfile subProfile) {
		SubProfileDTO dto = new SubProfileDTO();
		dto.setId(subProfile.getId());
		dto.setName(subProfile.getName());
		dto.setPhone(subProfile.getPhone());
		RelationDTO relationDTO = new RelationDTO();
		relationDTO.setId(subProfile.getRelation().getId());
		relationDTO.setName(subProfile.getRelation().getName());
		dto.setRelation(relationDTO);
		return dto;
	}
	
	public static void validateImageFile(MultipartFile file) {
		if (file.isEmpty())
			throw new ProfileException(IValidationConstants.EMPTY_IMAGE_FILE, HttpStatus.BAD_REQUEST);
		if (file.getSize() > 2 * 1024 * 1024)
			throw new ProfileException(IValidationConstants.IMAGE_SIZE_EXCEED, HttpStatus.PAYLOAD_TOO_LARGE);
		
		String contentType = file.getContentType();
		if (contentType == null || !ProfileUtil.isSupportedImageContentType(contentType))
			throw new ProfileException(IValidationConstants.ALLOWED_IMAGE_MESSAGE, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		String original = file.getOriginalFilename();
		if (original != null && !original.toLowerCase().matches(".*\\.(jpg|jpeg|png)$"))
			throw new ProfileException(IValidationConstants.INVALID_IMAGE_EXTENSION, HttpStatus.BAD_REQUEST);
	}
	
	public static String getFileName(Integer id, String imagePath, MultipartFile file) throws IOException {
		String fileName = id + "_ProfilePic_" + file.getOriginalFilename();
		Path filePath = Paths.get(imagePath + fileName);
		Files.write(filePath, file.getBytes());
		return fileName;
	}
}
