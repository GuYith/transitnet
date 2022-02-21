package whu.edu.cs.transitnet.Torch.base.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whu.edu.cs.transitnet.Torch.base.FileSetting;

public class NameEdgeIdLookup {
    private Logger logger = LoggerFactory.getLogger(NameEdgeIdLookup.class);
    private DBManager db;
    private FileSetting setting;

    public NameEdgeIdLookup(FileSetting setting) {
        this.setting = setting;
        db = new DBManager(setting);
    }

    public int[] get(String edgeName){
        String content = db.get(setting.EDGENAME_ID_TABLE, edgeName);
        if (content == null)
            return new int[0];
        String[] temp = content.split(",");
        int[] ret = new int[temp.length];
        for (int i = 0; i < temp.length; i++)
            ret[i] = Integer.valueOf(temp[i]);
        return ret;
    }


}
