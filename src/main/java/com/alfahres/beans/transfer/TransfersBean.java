package com.alfahres.beans.transfer;

import com.degla.db.models.Transfer;
import com.degla.system.SpringSystemBridge;
import com.degla.system.SystemService;
import com.degla.utils.GenericLazyDataModel;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Created by snouto on 12/09/15.
 */
public class TransfersBean implements Serializable {

    private GenericLazyDataModel<Transfer> transfers;

    private SystemService systemService;


    @PostConstruct
    private void onInit()
    {
        try
        {
            systemService = SpringSystemBridge.services();
            setTransfers(new GenericLazyDataModel<Transfer>(systemService.getTransferManager()));
        }catch (Exception s)
        {
            s.printStackTrace();
        }
    }


    public long getTotalCount()
    {
        if(getTransfers() != null)
            return getTransfers().getRowCount();

        else return 0;
    }

    public GenericLazyDataModel<Transfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(GenericLazyDataModel<Transfer> transfers) {
        this.transfers = transfers;
    }
}
