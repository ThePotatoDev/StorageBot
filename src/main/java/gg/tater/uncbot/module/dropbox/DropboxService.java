package gg.tater.uncbot.module.dropbox;

import com.dropbox.core.v2.DbxClientV2;

public interface DropboxService {

    DbxClientV2 getClient();

}
