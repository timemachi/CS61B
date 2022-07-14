package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {

    private String hashName;
    private String parentHash;
    private String date;
    private  String log;
    private HashMap<String, String> fileMap;  //nameOfFile -> hashOfBlob


    public Commit(String parentHash, String log, HashMap<String, String> fileMap){
        this.parentHash = parentHash;
        this.log = log;  //message of commit
        Date now = new Date();
        this.date = now.toString();
        this.fileMap = fileMap;   //nameOfFile -> hashOfBlob
        String hash = "";
        if (parentHash != null) {
            hash += parentHash + date + log;
        }
        this.hashName = sha1(hash);
    }

    /**
     * 返回该commit的独一无二的Hashname:由parent1的哈希值和时间戳合成。
     * @return
     */
    public String getHashName() {
        return hashName;
    }

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public String getDate() {
        return date;
    }
    public String getLog() {
        return log;
    }
    public Map<String, String> getFileMap(){
        return fileMap;
    }

}
