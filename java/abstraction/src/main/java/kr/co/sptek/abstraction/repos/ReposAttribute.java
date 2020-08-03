package kr.co.sptek.abstraction.repos;

public interface ReposAttribute {
    boolean useRepos(String repos);
    String remote();
    String getPath(String... paths);
}
