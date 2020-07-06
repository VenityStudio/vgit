package org.venity.vgit.controllers;

import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.venity.vgit.exceptions.AuthorizationException;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.RedirectException;
import org.venity.vgit.exceptions.UserDoesntExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;
import org.venity.vgit.services.AvatarService;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/user/avatar")
public class UserAvatarsAPIController extends AbstractController {
    private final AvatarService avatarService;
    private final UserCrudRepository userCrudRepository;

    public UserAvatarsAPIController(AvatarService avatarService, UserCrudRepository userCrudRepository) {
        this.avatarService = avatarService;
        this.userCrudRepository = userCrudRepository;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public void upload(HttpServletRequest httpServletRequest, @RequestBody MultipartFile file)
            throws AuthorizationException, InvalidFormatException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        if (!avatarService.upload(userPrototype, file))
            throw new InvalidFormatException();
    }

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] get(HttpServletRequest httpServletRequest) throws AuthorizationException, RedirectException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        try {
            return FileUtils.readFileToByteArray(avatarService.getAvatarFile(userPrototype.getId()));
        } catch (Exception e) {
            throw new RedirectException("https://www.gravatar.com/avatar/" + toMd5(userPrototype.getEmail()) + "?d=identicon&f=y");
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] get(@PathVariable String id)
            throws UserDoesntExistsException, RedirectException {
        try {
            return FileUtils.readFileToByteArray(avatarService.getAvatarFile(id));
        } catch (Exception e) {
            UserPrototype userPrototype = userCrudRepository.findById(id)
                    .orElseThrow(UserDoesntExistsException::new);

            throw new RedirectException("https://www.gravatar.com/avatar/" + toMd5(userPrototype.getEmail()) + "?d=identicon&f=y");
        }
    }

    @DeleteMapping
    public void delete(HttpServletRequest httpServletRequest) throws AuthorizationException, InvalidFormatException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        if (!avatarService.delete(userPrototype))
            throw new InvalidFormatException();
    }

    public String toMd5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array)
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return null;
        }
    }
}
