package kr.co.sptek.abstraction.repos;

public interface DeploymentExecutor {
    boolean useRepos(String repos);
    boolean exists(String path);
    void mkdirs(String dir);
    void decompress(String src, String dest, boolean remote_src);
    void permission(String mode, String... files);
    void execute(String comd);
    void flush();
}
