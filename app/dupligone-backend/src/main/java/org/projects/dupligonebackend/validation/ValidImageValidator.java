package org.projects.dupligonebackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public class ValidImageValidator implements ConstraintValidator<ValidImage, List<MultipartFile>> {

    private final Set<String> allowedMimeTypes = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext constraintValidatorContext) {
        if(files == null || files.isEmpty())
            return false;

        for(MultipartFile file : files){
            if(file == null || file.isEmpty()) return false;

            String mimeType = file.getContentType();
            if(mimeType == null || !allowedMimeTypes.contains(mimeType))
                return false;
        }

        return true;
    }
}
