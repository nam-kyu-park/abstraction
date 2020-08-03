package kr.co.sptek.abstraction.repos;

public interface ReposUploader {
    boolean useRepos(String repos);
    boolean exists(String path);
    void upload(String src, String dest);
    void mkdirs(String dir);
    void flush();
}
