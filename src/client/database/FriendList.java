package database;

import java.io.Serializable;

/**
 * 好友列表类，包括有几个分组以及分组列表类数组，均可直接访问
 * @author LightHouse
 *
 */
public class FriendList implements Serializable {
    private static final long serialVersionUID=1919810114514L;
    public int         groupNum;
    public GroupList[] Groups;

    public FriendList() {
        groupNum = 2;
        Groups = new GroupList[2];
        Groups[0] = new GroupList();
        Groups[1] = new GroupList();
        Groups[0].GroupName = "系统通知";
        Groups[0].FriendNum = 1;
        Groups[1].GroupName = "我的好友";
        Groups[1].FriendNum = 0;
        Groups[0].FriendAccount = new AccountInfo[1];
        AccountInfo acif = new AccountInfo();
        acif.id = 100000;
        Groups[0].FriendRemarks = new String[1];
        Groups[0].FriendAccount[0] = acif;
        Groups[0].FriendRemarks[0] = null;
    }

    public FriendList(int num) {
        if(num < 2) return;
        groupNum = num;
        Groups = new GroupList[num];
        for(int i = 0; i < num; ++i) {
            Groups[i] = new GroupList();
        }
        Groups[0].GroupName = "系统通知";
        Groups[0].FriendNum = 1;
        Groups[1].GroupName = "我的好友";
        Groups[1].FriendNum = 0;
        Groups[0].FriendAccount = new AccountInfo[1];
        AccountInfo acif = new AccountInfo();
        acif.id = 100000;
        Groups[0].FriendRemarks = new String[1];
        Groups[0].FriendAccount[0] = acif;
        Groups[0].FriendRemarks[0] = null;
    }

    public int[] Query(int fid) {
        int[] ans = new int[2];
        int     i = 0;
        int     j = 0;
        outer:
        for(i = 1; i < groupNum; ++i) {
            for(j = 0; j < Groups[i].FriendNum; ++j) {
                if(fid == Groups[i].FriendAccount[j].id) break outer;
            }
        }

        ans[0] = i;
        ans[1] = j;
        return ans;
    }
}
