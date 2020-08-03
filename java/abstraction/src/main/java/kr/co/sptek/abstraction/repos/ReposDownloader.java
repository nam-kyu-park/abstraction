package kr.co.sptek.abstraction.repos;

public interface ReposDownloader {
    boolean useRepos(String repos);
    boolean exists(String path);
    void download(String src, String dest);
    void mkdirs(String dir);
}
