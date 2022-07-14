package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import gitlet.Utils;

import static gitlet.Utils.*;

public class Gitlet implements Serializable{
    private HashMap<String, String> branches = new HashMap<>();
    private String head; //commit的哈希值
    private String headBranch;
    private String workingDir;

    private HashMap<String, String> stageAdd = new HashMap<>(); //Blobs的哈希值
    private ArrayList<String> stageRemove = new ArrayList<>(); //Blob的哈希值
    private ArrayList<String> allCommitsIDs = new ArrayList<>();
    private HashMap<String, String> shortCommitsIDs = new HashMap<>();

    /**
     * Gitlet.class文件：用来记录这个版本的gitlet的信息（head，added files, all commitsIDs, short commitsIDs, etc.
     */
    public Gitlet() {
        //if file exists, load gitlet object if not create new one
        workingDir = System.getProperty("user.dir"); //Current working directory of User, see https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
        File prevState = new File(workingDir, ".gitlet/gitletState"); //gitlet文件夹下面有一个专门的文件用来记录gitlet的state
        if (prevState.exists()) {
            Gitlet prevGitlet = readObject(prevState, Gitlet.class); //如果存在gitletState这个文件，打开它。
            this.branches = prevGitlet.branches;
            this.head = prevGitlet.head;
            this.headBranch = prevGitlet.headBranch;
            this.workingDir = prevGitlet.workingDir;
            this.stageAdd = prevGitlet.stageAdd;
            this.stageRemove = prevGitlet.stageRemove;
            this.allCommitsIDs = prevGitlet.allCommitsIDs;
            this.shortCommitsIDs = prevGitlet.shortCommitsIDs;

        }
    }

    public void saveState() {
        File newGitlet = new File(workingDir, "gitlet/gitletState");
        writeObject(newGitlet, this); //将这个gitlet的信息写入gitletState文件（更新）
    }

    public void init() {
        HashMap<String, String> empty = new HashMap<>(); //一个空的hashmap
        Commit initialCommit = new Commit(null, "initial commit", empty);

        File gitletDir = new File(workingDir, ".gitlet");
        if (gitletDir.exists()) {
            throw new GitletException("A gitlet version-control system already exists in the current directory.");
        }

        gitletDir.mkdir();

        File newCommit = new File(workingDir, ".gitlet/" + initialCommit.getHashName()); //文件名：Commit的HashName，直接放在.gitlet里面
        writeObject(newCommit, initialCommit); //引入initCommit

        branches.put("master", initialCommit.getHashName());
        head = branches.get("master");
        headBranch = "master";
        allCommitsIDs.add(initialCommit.getHashName()); //加入此commit的hashname

        saveState();
    }

    public void add(String fileName) {
        File file = new File(workingDir, fileName);

        if (!file.exists()) {
            throw new GitletException("File does not exist.");
        }

        if (stageRemove.contains(fileName)) {  //如果stageRemove列表里有这个文件：从列表上移除。
            stageRemove.remove(fileName);
            saveState();
            return;
        }

        //get hash
        String hash = fileName + sha1(readContents(file));  //文件读出来后再编码，也是blob文件的文件名
        File temp = new File(workingDir, ".gitlet/" + head);  //commit文件：head（当前在哪个版本）
        Commit target = readObject(temp, Commit.class);  //把当前commit打开
        //如果这个文件已经在stageAdd里，并且哈希值也一样：已经添加
        if (stageAdd.containsKey(fileName) && stageAdd.get(fileName).equals(hash)) {
            throw new GitletException("No change added");
        }
        //如果这个文件和当前commit里的同名文件的内容一样的话：保存这个gitlet并返回，什么也不需要做。
        if (hash.equals(target.getFileMap().get(fileName))) {
            saveState();
            return;
        }

        File blob = new File(workingDir, ".gitlet/" + hash);
        writeContents(blob, readContents(file));   //把文件写到blob里：以哈希值命名，但是用byte[]读取并写入
        stageAdd.put(fileName, hash);  //key = filename, value = 哈希值（文件名+内容的sha1编码，同时也是其blob文件的文件名）

        saveState();
    }

    public void commit(String args) {
        if (stageAdd.isEmpty() && stageRemove.isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }
        String log = args;
        HashMap<String, String> blobMap = new HashMap<>();
        //复制现在的commit里的hashmap，然后把stageAdd里面的内容加进去，生成新的commit
        File temp = new File(workingDir, ".gitlet/" + head);
        Commit parent = readObject(temp, Commit.class); //打开上一个commit：这一个的parent

        if (parent.getFileMap() != null) {
            blobMap.putAll(parent.getFileMap());  //如果父commit里有文件，把这些文件全部放到子commit里面。
        }

        blobMap.putAll(stageAdd);  //再把stageAdd里面的文件放到blobmap里面
        for (String i : stageRemove) {
            blobMap.remove(i);  //把stageRemove名单上的文件从新的blobMap里面清除，因为他们被移除了
        }

        stageAdd = new HashMap<>();  //清空stageAdd
        stageRemove = new ArrayList<>();  //清空stageRemove

        Commit c = new Commit(head, log, blobMap);   //新的commit：head是上一个commit的哈希值

        File newCommit = new File(workingDir, ".gitlet/" + c.getHashName());  //创建新的commit路径
        writeObject(newCommit, c);  //把新commit写入文件中

        allCommitsIDs.add(c.getHashName()); //更新所有commit的id链表
        String shortID = c.getHashName().substring(0, 6);
        shortCommitsIDs.put(shortID, c.getHashName()); //更新shortID

        head = c.getHashName();  //更新head

        branches.put(headBranch, head);  //更新head branch: headbranch内容是“master”， head是目前Commit的哈希值

        saveState();
    }

    public void rm(String fileName) {
        if (stageRemove.contains(fileName)){
            throw new GitletException("Already in remove list.");
        }

        if (stageAdd.containsKey(fileName)) {
            stageAdd.remove(fileName);
            saveState();
            return;
        }

        File temp = new File(workingDir, ".gitlet/" + head);  //head commit
        Commit target = readObject(temp, Commit.class);
        File fileToDelete = new File(workingDir, fileName);

        if (target.getFileMap().containsKey(fileName)) { //如果head commit的文件里面有这个文件名：
            stageRemove.add(fileName);                  //把这个文件加到remove名单里

            if (fileToDelete.exists()) {
                fileToDelete.delete();                  //如果这个文件存在：删掉它
            }
            if (stageAdd.containsKey(fileName)) {
                stageAdd.remove(fileName);              //如果在add名单里，将它拿掉
            }
            saveState();
            return;
        } else if (stageAdd.containsKey(fileName)) {    //如果不在head commit的名单列表里；
            stageAdd.remove(fileName);                  //但是在stageAdd链表里：删掉它
            saveState();
            return;
        }

        throw new GitletException("No reason to remove the file."); //如果都不在：就没必要删掉它
    }

    public void checkout(String[] args) {
        switch (args.length) {
            case 2:
                checkoutBranch(args[1]);
                saveState();
                break;

            case 3:
                checkoutFile(args[2]);
                saveState();
                break;

            case 4:
                if (!args[2].equals("--")) {
                    throw new GitletException("Incorrect operands");
                }
                checkoutCommit(args[1], args[3]);
                saveState();
                break;
        }
    }

    private void checkoutBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            throw new GitletException("No such branch exist.");
        }
        if (branches.get(branchName) == head) {
            throw new GitletException("No need to checkout the current branch.");
        }
        File temp = new File(workingDir, ".gitlet/" + branches.get(branchName));
        Commit branchCommit = readObject(temp, Commit.class); //targeted commit
        File temp2 = new File(workingDir, ".gitlet/" + head);
        Commit currBranch = readObject(temp2, Commit.class); //current commit

        //Check for untracked files
        for (String i : plainFilenamesIn(workingDir)) {  //当前工作文件夹中的文件列表
            File file = new File(workingDir, i);
            String fileHash = i + sha1(readContents(file));
            if (branchCommit.getFileMap().containsKey(i)                    //目标branch里面有这个文件；
                    && !branchCommit.getFileMap().get(i).equals(fileHash)   //目标文件与现在这个文件不一样；
                    && !currBranch.getFileMap().containsKey(i)              //现在的branch里面没有这个文件
                    && !i.equals(".gitignore")                              //不是两个特定文件
                    && !i.equals("proj2.iml")) {
                System.exit(0);
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

        ArrayList<String> holder = new ArrayList<>();
        //对每个当前branch里的文件：
        for (String a: currBranch.getFileMap().keySet()) {
            //如果当前文件夹里的文件存在于branch里:替换掉
            if (branchCommit.getFileMap().containsKey(a)) {  //如果目标branch里也有这个文件
                String blobHash = branchCommit.getFileMap().get(a);  //目标branch里这个文件的哈希名是blobhash
                File blobFile = new File(workingDir, ".gitlet/" + blobHash); //这个blob的地址
                File fileToReplace = new File(workingDir, a); //这个文件应该在的地址
                writeContents(fileToReplace, readContents(blobFile)); //写入：不管是不是相同的，不重要
            }
            //如果当前文件夹里的文件不存在于branch里：直接删除
            File fileToDelete = new File(workingDir, a);
            fileToDelete.delete();
        }

        //如果branch里的文件不存在于当前文件夹里：创建新文件
        for (String a : branchCommit.getFileMap().keySet()) {
            if (!holder.contains(a)) {
                String blobHash = branchCommit.getFileMap().get(a);
                File blobFile = new File(workingDir, ".gitlet/" + blobHash);
                File fileToAdd = new File(workingDir, a);
                writeContents(fileToAdd, readContents(blobFile));
            }
        }
        head = branches.get(branchName);
        headBranch = branchName;

        saveState();
    }

    private void checkoutFile(String fileName) {
        checkoutCommit(head, fileName);
    }

    private void checkoutCommit(String commitID, String fileName) {
        if (!allCommitsIDs.contains(commitID) && !shortCommitsIDs.containsKey(commitID)) {
            throw new GitletException("No commit with that id exists");
        }

        if (allCommitsIDs.contains(commitID) || shortCommitsIDs.containsKey(commitID)) {
            if (shortCommitsIDs.containsKey(commitID)) {
                commitID = shortCommitsIDs.get(commitID);
            }
        }
        File temp = new File(workingDir, ".gitlet/" + commitID);
        Commit target = readObject(temp, Commit.class);

        if (!target.getFileMap().containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        }

        File blobFile = new File(workingDir, ".gitlet/" + target.getFileMap().get(fileName));
        File fileToReplace = new File(workingDir, fileName);
        writeContents(fileToReplace, readContents(blobFile));
    }

    public void log() {
        logHelper(head);
        saveState();
    }

    public void globallog() {
        for (String commmitID : allCommitsIDs) {
            File temp = new File(workingDir, ".gitlet/" + commmitID);
            Commit curr = readObject(temp, Commit.class);
            System.out.println("===");
            System.out.println("commit " + curr.getHashName());
            System.out.println("Date: " + curr.getDate());
            System.out.println(curr.getLog());
            System.out.println("");
        }
        saveState();
    }
    private void logHelper(String currCom) {
        File temp = new File(workingDir, ".gitlet/" + currCom);
        Commit curr = readObject(temp, Commit.class);  //目前的commit文件
        // recursion methode to print
        if (curr.getParentHash() == null) {
            System.out.println("===");
            System.out.println("commit " + curr.getHashName());
            System.out.println("Date: " + curr.getDate());
            System.out.println(curr.getLog());
            System.out.println("");
            return;
        }
        System.out.println("===");
        System.out.println("commit " + curr.getHashName());
        System.out.println("Date: " + curr.getDate());
        System.out.println(curr.getLog());
        System.out.println("");
        logHelper(curr.getParentHash());
    }

    public void find(String msg) {
        boolean found = false;
        for (String commmitID : allCommitsIDs) {
            File temp = new File(workingDir, ".gitlet/" + commmitID);
            Commit curr = readObject(temp, Commit.class);
            if (curr.getLog().contains(msg)) {
                System.out.println(curr.getHashName());
                found = true;
            }
        }
        if (!found) {
            throw new GitletException("Found no commit with that message.");
        }
        saveState();
    }

    public void status() {
        System.out.println("=== Branches ===");
        List<String> sArray = new ArrayList<String>(branches.keySet());
        Collections.sort(sArray);

        for (String i : sArray) {
            if (i.equals(headBranch)) {
                System.out.println("*" + i);
            } else {
                System.out.println(i);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        sArray = new ArrayList<String>(stageAdd.keySet());
        java.util.Collections.sort(sArray);
        for (String i : sArray) {
            System.out.println(i);
        }

        System.out.println();

        System.out.println("=== Removed Files ===");
        Collections.sort(stageRemove);
        for (String i : stageRemove) {
            System.out.println(i);
        }

        System.out.println();

        saveState();
    }

    private HashMap<String, String> branchToIntCom = new HashMap<>(); //Branch --> SHA1
    private int count; // number of other branch
    public void branch(String branchName) {
        if (branches.containsKey(branchName)) {
            throw new GitletException("A branch with that name already exists.");
        }
        branches.put(branchName, head);

        branchToIntCom.put(branchName, head);
        count ++;
        saveState();
    }

    public void rmBranch(String branchName) {
        List<String> Branches = new ArrayList<String>(branches.keySet());
        if (!Branches.contains(branchName)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (branches.get(branchName) == head) {
            throw new GitletException("Cannot remove the current branch.");
        }
        branches.remove(branchName);
        branchToIntCom.remove(branchName);
        count --;
        saveState();
    }

    public void reset(String CommitID) {
        //如果commit不存在：报错
        if (!allCommitsIDs.contains(CommitID) && !shortCommitsIDs.containsKey(CommitID)) {
            throw new GitletException("No commit with that id exists.");
        }
        //确定完整的commit哈希名称
        if (shortCommitsIDs.containsKey(CommitID)) {
            CommitID = shortCommitsIDs.get(CommitID);
        }
        File tem = new File(workingDir, ".gitlet/" + CommitID);
        Commit resetCommit = readObject(tem, Commit.class);
        File temp = new File(workingDir, ".gitlet/" + head);
        Commit currCommit = readObject(temp, Commit.class);

        //如果cwd里有文件没被track，而且会被overwritten（也就是被目标commit里面的文件取代），报错
        for (String a : plainFilenamesIn(workingDir)) {
            File file = new File(workingDir, a);
            String fileHash = a + sha1(readContents(file));
            if (resetCommit.getFileMap().containsKey(a)                      // reset commit里面有相同名字的文件；
                    && !resetCommit.getFileMap().get(a).equals(fileHash)   //  这个文件和reset commit里的内容不一样；
                    && !currCommit.getFileMap().containsKey(a)){            //这个文件在目前commit中没有（没被track）
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }                                                               //如果reset，这个文件就没了。（但是如果在reset commit里面没有，则不会被覆盖）
        }

        //开始reset
        for (String a: resetCommit.getFileMap().keySet()) {
            checkoutCommit(CommitID, a);
        }

        //清除stage Area
        stageAdd = new HashMap<>();
        head = CommitID;
        branches.put(headBranch, CommitID);

        saveState();

    }
    //输入一个commit，给出它的branch history（从给定commit到init）
    private ArrayList<String> branchHistory(Commit commit) {
        ArrayList<String> branchHistory = new ArrayList<>();
        while (commit.getParentHash() != null) {
            branchHistory.add(commit.getHashName());
            File temp = new File(workingDir, ".gitlet/" + commit.getParentHash());
            Commit parent = readObject(temp, Commit.class);
            commit = parent;
        }
        return branchHistory;
    }
    private String findSplitPoint(String givenBranch) { //给定的branch与head branch的split point
        if(!branches.containsKey(givenBranch)) {
            throw new GitletException("A branch with that name does not exist.");
        }
        if (givenBranch.equals(headBranch)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }
        File temp = new File(workingDir, ".gitlet/" + branches.get(givenBranch));
        Commit givenCommit = readObject(temp, Commit.class);
        File temp2 = new File(workingDir, ".gitlet/" + head);
        Commit currCommit = readObject(temp2, Commit.class);
        String splitPoint = "";
        ArrayList<String> givenBranchHistory = branchHistory(givenCommit);
        ArrayList<String> HeadBranchHistory = branchHistory(currCommit);
        boolean found = false;
        for (int x = 0; x < givenBranchHistory.size() && !found; x++) {
            for (int y = 0; y < HeadBranchHistory.size() && !found; y++) {
                if (givenBranchHistory.get(x).equals(HeadBranchHistory.get(y))) {
                    found = true;
                    splitPoint = givenBranchHistory.get(x);
                }
            }
        }
        return splitPoint;
    }

    public void merge(String branchName) {
        if (!stageAdd.isEmpty() || !stageRemove.isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }
        //已经确认：1.这个branch存在；2：这两个不是一个branch
        String splitPoint = findSplitPoint(branchName);
        File temp = new File(workingDir, ".gitlet/" + branches.get(branchName));
        Commit givenCommit = readObject(temp, Commit.class);
        File temp2 = new File(workingDir, ".gitlet/" + head);
        Commit currCommit = readObject(temp2, Commit.class);
        ArrayList<String> givenBranchHistory = branchHistory(givenCommit);
        ArrayList<String> HeadBranchHistory = branchHistory(currCommit);
        //两个exception：在之前和在之后。
        //第一个：分歧点在给的commit：do nothing; the merge is complete.
        if (givenBranchHistory.get(0) == splitPoint) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (HeadBranchHistory.get(0) == splitPoint) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        File temp3 = new File(workingDir, ".gitlet/" + splitPoint);
        Commit split = readObject(temp3, Commit.class);
        //Untracked file
        for (String a : plainFilenamesIn(workingDir)) {
            if (!split.getFileMap().containsKey(a) && !a.equals(".gitignore")
                    && !a.equals("proj2.iml")) {
                throw new GitletException("There is an untracked file in the way;delete it or add it first.");
            }
        }
        //所有例外处理完，开始正题
        //拿到所有split里面的文件，一个个开始对比
        Map<String, String> splitFiles =  split.getFileMap();
        Map<String, String> currFiles =  currCommit.getFileMap(); //head,现在所在的branch
        Map<String, String> givenFiles =  givenCommit.getFileMap();//目标branch，要和它merge
        Set filesName = currFiles.keySet(); //目前文件夹里的文件名字：应该和另外两个是一样的
        Iterator<String> files = filesName.iterator();
        while (files.hasNext()) {
            String fileName = files.next();
            String splitFileHash = splitFiles.get(fileName);
            String currFileHash = currFiles.get(fileName);
            String givenFileHash = givenFiles.get(fileName);

            //Any files that have been modified in the given branch since the split point, but not modified in the
            // current branch since the split point should be changed to their versions in the given branch
            // (checked out from the commit at the front of the given branch). These files should then all
            // be automatically staged.
            if (!splitFileHash.equals(null) && splitFileHash.equals(currFileHash) && !splitFileHash.equals(givenFileHash)
                    && !givenFileHash.equals(null)) {

            }

            //Any files that were not present at the split point and are present only in the current
            // branch should remain as they are.
            if (splitFileHash.equals(null) && givenFileHash.equals(null) && !currFileHash.equals(null)) {
                continue;
            }
            //Any files that have been modified in the current branch but not in the given branch
            // since the split point should stay as they are.
            if (!splitFileHash.equals(currFileHash) && splitFileHash.equals(givenFileHash) && !splitFiles.equals(null)) {
                continue;
            }
            //Any files that were not present at the split point and are present only in the given
            // branch should be checked out and staged.
            if (splitFileHash.equals(null) && currFileHash.equals(null) && !givenFileHash.equals(null)) {
                File file = new File(workingDir, fileName);
                add(file.getName());
                checkoutFile(file.getName());
                continue;
            }
            //Any files present at the split point, unmodified in the current branch, and
            // absent in the given branch should be removed (and untracked).
            if (!splitFileHash.equals(null) && splitFileHash.equals(currFileHash) && givenFileHash.equals(null)) {
                File file = new File(workingDir, fileName);
                rm(file.getName());
            }
            //Any files present at the split point, unmodified in the given branch, and absent in the current branch
            // should remain absent.
            if (!splitFileHash.equals(null) && splitFileHash.equals(givenFileHash) && currFileHash.equals(null)) {
                continue;
            }

        }
        saveState();







    }


}
