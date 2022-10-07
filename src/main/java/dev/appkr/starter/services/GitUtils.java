package dev.appkr.starter.services;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class GitUtils {

  public static void init(String path, String message) throws GitAPIException {
    // Reference: https://www.vogella.com/tutorials/JGit/article.html
    final Git git = Git.init().setDirectory(new File(path)).call();
    git.add().addFilepattern(".").call();
    git.commit().setMessage(message).call();
  }
}
