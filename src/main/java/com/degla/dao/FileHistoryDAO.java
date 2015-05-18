package com.degla.dao;

import com.degla.db.models.FileHistory;
import org.springframework.stereotype.Component;

/**
 * Created by snouto on 18/05/15.
 */
@Component("fileHistoryDAO")
public class FileHistoryDAO extends AbstractDAO<FileHistory> {
    @Override
    public String getEntityName() {
        return "fileHistoryDAO";
    }
}
