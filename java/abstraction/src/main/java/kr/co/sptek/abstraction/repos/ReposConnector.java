package kr.co.sptek.abstraction.repos;

import com.jcraft.jsch.SftpException;
import kr.co.sptek.abstraction.module.ansible.AnsibleConnector;
import kr.co.sptek.abstraction.module.ftp.FTPConnector;
import kr.co.sptek.abstraction.module.shell.ShellCommander;
import kr.co.sptek.abstraction.properties.Repos.RemoteProperties;
import kr.co.sptek.abstraction.properties.Repos.ReposEntry;
import kr.co.sptek.abstraction.properties.ResourceProperties;
import kr.co.sptek.abstraction.util.Zipper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.CompressionType;
import sptek.dcim.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ReposConnector implements ReposUploader, ReposDownloader, ReposAttribute, DeploymentExecutor {

    private static final Logger logger = LogManager.getLogger(ReposConnector.class);

    private ReposEntry entry;
    private String useRepos;
    private String reposType;
    private int reposIndex;
    private Map<String, Object> conn;
    private ResourceProperties resource;

    public ReposConnector(ResourceProperties resource) {
        this.resource = resource;
        conn = new HashMap<String, Object>();
    }

    public void create(ReposEntry entry) {
        String remoteName = "(null)";
        try {
            this.entry = entry;

            for( RemoteProperties remote : entry.getProperties()) {

                remoteName = remote.getName();
                String type = remote.getType();
                if (type.toLowerCase().equals("ftp")) {
                    conn.put(remote.getName(), createFTP(remote));
                    logger.info("insert ftp connector to conn");
                }

                if (type.toLowerCase().equals("ansible")) {
                    conn.put(remote.getName(), createAnsible(remote));
                    logger.info("insert ansible connector to conn");
                }

                if (type.toLowerCase().equals("system")) {
                    // DOTO:
                }
            }

        } catch (Exception e) {
            logger.warn(String.format("Not create resource entry %s: %s", remoteName, e.getMessage()));
        }
    }

    public FTPConnector createFTP(RemoteProperties remote) throws Exception {
        FTPConnector ftpConnector = new FTPConnector();
        ftpConnector.init(remote.getServer(),
                remote.getPort(),
                remote.getUser(),
                remote.getPassword());
        return ftpConnector;
    }

    public AnsibleConnector createAnsible(RemoteProperties remote) throws Exception {
        AnsibleConnector ansibleConnector = new AnsibleConnector(resource);

        String entry = remote.getIncludeEntry();
        if(entry != null && !entry.isEmpty())
            ansibleConnector.init(entry);
        else {
            ansibleConnector.init(remote);
        }
        return ansibleConnector;
    }

    @Override
    public boolean useRepos(String repos){
        try {
            useRepos = repos;
            reposIndex = entry.indexOf(repos);
            reposType = entry.getType(reposIndex).trim();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return !reposType.isEmpty();
    }

    @Override
    public boolean exists(String path) {
        return false;
    }


    @Override
    public void download(String src, String dest) {
        try {
            String remote = entry.getRemote(reposIndex);

            if (reposType.toLowerCase().equals("ftp")) {
                FTPConnector connector = (FTPConnector)conn.get(useRepos);
                connector.download(remote, src, dest);
            }
            else if (reposType.toLowerCase().equals("system")) {
                if(src.lastIndexOf("*") > 0) {
                    String sub = src.substring(0, src.lastIndexOf("/"));
                    String srcPath = remote + "/" + sub;
                    FileUtils.copyDirectory(new File(srcPath), new File(dest));
                }
                else
                    FileUtils.copyFileToDirectory(new File(remote + "/" +src), new File(dest));
            }
            else {
                logger.warn(String.format("%s is not provide download function.", reposType.toUpperCase()));
            }
        }
        catch (SftpException e) {
            logger.warn(String.format("Not download resource %s: %s", useRepos, e.getMessage()));
        }
        catch (IOException e) {
            logger.warn(String.format("Not download resource %s: %s", useRepos, e.getMessage()));
        }
        catch (ArrayIndexOutOfBoundsException e) {
            logger.warn(String.format("Not find remote entry resource %s: %s", useRepos,e.getMessage()));
        }
    }

    @Override
    public void upload(String src, String dest) {
        try {
            String remote = entry.getRemote(reposIndex);

            if (reposType.toLowerCase().equals("ftp")) {
                FTPConnector connector = (FTPConnector)conn.get(useRepos);
                connector.upload(remote + "/" + dest, src);
            }

            if (reposType.toLowerCase().equals("ansible")) {
                AnsibleConnector connector = (AnsibleConnector)conn.get(useRepos);
                connector.upload(remote + "/" + dest, src);
            }

            if (reposType.toLowerCase().equals("system")) {
                File file = new File(src);
                if (file.isDirectory()){
                    FileUtils.copyDirectory(file, new File(remote + "/" + dest ), false);
                } else {
                    FileUtils.copyFileToDirectory(file, new File(remote + "/" + dest), false);
                }

                //FileUtils.copyFileToDirectory(new File(src), new File(remote + "/" + dest), false);
            }
        }
        catch (SftpException e) {
            logger.warn(String.format("Not upload resource %s: %s", useRepos, e.getMessage()));
        }
        catch (Exception e) {
            logger.warn(String.format("Not upload resource %s: %s", useRepos, e.getMessage()));
        }
//        catch (IOException e) {
//            logger.warn(String.format("Not upload resource %s: %s", useRepos, e.getMessage()));
//        }
//        catch (ArrayIndexOutOfBoundsException e) {
//            logger.warn(String.format("Not find remote entry resource %s: %s", useRepos,e.getMessage()));
//        }
    }

    @Override
    public String remote() {
        return entry.getRemote(reposIndex);
    }

    @Override
    public String getPath(String...paths) {
        String path = Paths.get(remote(), paths).toString();
        return FilenameUtils.separatorsToUnix(path);
    }

    @Override
    public void mkdirs(String dir) {
        try {
            String remote = entry.getRemote(reposIndex);

            if (reposType.toLowerCase().equals("ftp")) {
                FTPConnector connector = (FTPConnector)conn.get(useRepos);
                connector.mkdir(remote + "/" + dir);
            }

            if (reposType.toLowerCase().equals("ansible")) {
                AnsibleConnector connector = (AnsibleConnector)conn.get(useRepos);
                connector.mkdir(remote + "/" + dir);
            }

            if (reposType.toLowerCase().equals("system")) {
                FileUtils.forceMkdir(new File(remote + "/" + dir));
            }
        }
        catch (SftpException e) {
            logger.warn(String.format("Not create directory %s: %s", useRepos, e.getMessage()));
        }
        catch (IOException e) {
            logger.warn(String.format("Not create directory %s: %s", useRepos, e.getMessage()));
        }
        catch (ArrayIndexOutOfBoundsException e) {
            logger.warn(String.format("Not find remote entry resource %s: %s", useRepos,e.getMessage()));
        }
    }

    @Override
    public void decompress(String src, String dest, boolean remote_src) {
        try {
            String remote = entry.getRemote(reposIndex);

            if (reposType.toLowerCase().equals("ansible")) {
                AnsibleConnector connector = (AnsibleConnector)conn.get(useRepos);
                String destFile = remote + "/" + dest;
                String srcFile = (remote_src) ? remote + "/" + src : src;
                connector.decompress(destFile, srcFile, remote_src);
            }
            else if (reposType.toLowerCase().equals("system")) {
                Zipper.decompress(src, remote + "/" + dest, ArchiveFormat.TAR, CompressionType.GZIP);
            }
            else {
                logger.warn(String.format("%s is not provide decompress function.", reposType.toUpperCase()));
            }
        }
        catch (Exception e) {
            logger.warn(String.format("Not provide decompress function. %s: %s", useRepos, e.getMessage()));
        }
    }

    @Override
    public void execute(String cmd) {
        try {
            if (reposType.toLowerCase().equals("ansible")) {
                AnsibleConnector connector = (AnsibleConnector)conn.get(useRepos);
                connector.execute(cmd.trim());
            }
            else if (reposType.toLowerCase().equals("system")) {
                ShellCommander shell = new ShellCommander();
                shell.execute(cmd.trim());
            }
            else {
                logger.warn(String.format("%s is not provide execute function.", reposType.toUpperCase()));
            }
        }
        catch (Exception e) {
            logger.warn(String.format("Not provide execute function. %s: %s", useRepos, e.getMessage()));
        }
    }

    @Override
    public void permission(String mode, String...files) {
        try {
            String remote = entry.getRemote(reposIndex);

            StringBuilder builder = new StringBuilder();
            for(String file : files)
                builder.append(remote + "/" + file + " ");

            if (reposType.toLowerCase().equals("ansible")) {
                AnsibleConnector connector = (AnsibleConnector)conn.get(useRepos);
                connector.permission(mode, builder.toString().split(" "));
            }
            else {
                logger.warn(String.format("%s is not provide permission function.", reposType.toUpperCase()));
            }
        }
        catch (Exception e) {
            logger.warn(String.format("Not provide permission function. %s: %s", useRepos, e.getMessage()));
        }
    }

    @Override
    public void flush() {
        if (reposType.toLowerCase().equals("ansible")) {
            AnsibleConnector connector = (AnsibleConnector)conn.get(useRepos);
            connector.flush();
        }
    }
}
