package org.venity.vgit.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.venity.vgit.configuration.ApplicationConfiguration;
import org.venity.vgit.prototypes.UserPrototype;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class AvatarService {
    private final ApplicationConfiguration applicationConfiguration;

    public AvatarService(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public synchronized boolean upload(UserPrototype userPrototype, MultipartFile file) {
        if (!isSupported(Objects.requireNonNull(file.getContentType())))
            return false;

        if (!delete(userPrototype))
            return false;

        var avatarFile = getAvatarFile(userPrototype.getId());

        try {
            if (!avatarFile.createNewFile())
                return false;
        } catch (IOException e) {
            return false;
        }

        try {
            file.transferTo(avatarFile);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public synchronized boolean delete(UserPrototype userPrototype) {
        var avatarFile = getAvatarFile(userPrototype.getId());

        if (avatarFile.exists())
            return avatarFile.delete();

        return true;
    }

    public File getAvatarFile(String avatar) {
        return new File(getAvatarRoot(), String.valueOf(avatar)).getAbsoluteFile();
    }

    private File getAvatarRoot() {
        var root = new File(applicationConfiguration.getProperty("storage.avatar",
                "./storage/avatar")).getAbsoluteFile();

        if (!root.exists())
            root.mkdirs();

        return root;
    }

    private boolean isSupported(String contentType) {
        return contentType.equals("image/jpg") || contentType.equals("image/jpeg");
    }
}
