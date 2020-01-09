package org.venity.vgit;

import java.util.regex.Pattern;

public interface VGitRegex {
    Pattern LOGIN_PATTERN = Pattern.compile("^(?=.{5,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
    Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");
    Pattern FULL_NAME_PATTERN = Pattern.compile(".{4,20}");
    Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    Pattern GIT_URL_PATTERN = Pattern.compile("^/[A-Za-z0-9-_]+/[A-Za-z0-9-_]+\\.git/.+");
    Pattern GIT_REPOSITORY_PATTERN = Pattern.compile("^(?=.{2,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$");
}
