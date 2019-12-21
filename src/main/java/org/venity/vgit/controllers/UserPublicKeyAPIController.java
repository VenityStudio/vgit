package org.venity.vgit.controllers;

import com.github.fommil.ssh.SshRsaCrypto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.venity.vgit.exceptions.AuthorizationException;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.KeyAlreadyExistsException;
import org.venity.vgit.exceptions.KeyDoesntExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;

import javax.servlet.http.HttpServletRequest;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/publickey")
public class UserPublicKeyAPIController extends AbstractController {
    private static final SshRsaCrypto rsa = new SshRsaCrypto();
    private final UserCrudRepository userCrudRepository;

    public UserPublicKeyAPIController(UserCrudRepository userCrudRepository) {
        this.userCrudRepository = userCrudRepository;
    }

    @PostMapping
    public void add(HttpServletRequest httpServletRequest, @RequestBody AddPublicKeyBody body)
            throws AuthorizationException, InvalidFormatException, KeyAlreadyExistsException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);
        PublicKey publicKey;

        if (body.getName() == null)
            throw new InvalidFormatException();

        if (body.getKey() == null)
            throw new InvalidFormatException();

        if (userPrototype.getPublicKeys().containsKey(body.getName()))
            throw new KeyAlreadyExistsException();

        try {
            publicKey = rsa.readPublicKey(rsa.slurpPublicKey(body.getKey()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidFormatException();
        }

        var keys = userPrototype.getPublicKeys();
        keys.put(body.getName(), publicKey);
        userPrototype.setPublicKeys(keys);

        userCrudRepository.save(userPrototype);
    }

    @DeleteMapping
    public void delete(HttpServletRequest httpServletRequest, @RequestBody DeletePublicKeyBody body)
            throws AuthorizationException, InvalidFormatException, KeyDoesntExistsException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        if (body.getName() == null)
            throw new InvalidFormatException();

        if (!userPrototype.getPublicKeys().containsKey(body.getName()))
            throw new KeyDoesntExistsException();

        var keys = userPrototype.getPublicKeys();
        keys.remove(body.getName());
        userPrototype.setPublicKeys(keys);

        userCrudRepository.save(userPrototype);
    }

    @GetMapping
    public Map<String, String> get(HttpServletRequest httpServletRequest) throws AuthorizationException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);
        var map = new HashMap<String, String>();
        userPrototype.getPublicKeys().forEach((name, key) -> {
            StringBuilder builder = new StringBuilder(key.getEncoded().length* 2);
            for (int i = 33; i < key.getEncoded().length; i++) {
                if (i >= 50) break;
                builder.append(String.format("%02x:", key.getEncoded()[i]));
            }

            String formattedKey = builder.toString();
            map.put(name, formattedKey.substring(0, formattedKey.length() - 1));
        });
        return map;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddPublicKeyBody {
        private String name;
        private String key;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeletePublicKeyBody {
        private String name;
    }
}
