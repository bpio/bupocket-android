package com.bupocket.database.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.bupocket.http.api.AddressBookListBean;
import com.bupocket.model.BlockInfoRespBoBean;
import com.bupocket.model.ImageInfo;
import com.bupocket.model.LogListModel;
import com.bupocket.model.NodeBuildModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.model.TokenTxInfo;
import com.bupocket.model.TxDetailRespBoBean;
import com.bupocket.model.TxInfoRespBoBean;
import com.bupocket.voucher.model.VoucherDetailModel;

import com.bupocket.database.greendao.AddressBookListBeanDao;
import com.bupocket.database.greendao.BlockInfoRespBoBeanDao;
import com.bupocket.database.greendao.ImageInfoDao;
import com.bupocket.database.greendao.LogListModelDao;
import com.bupocket.database.greendao.NodeBuildModelDao;
import com.bupocket.database.greendao.SuperNodeModelDao;
import com.bupocket.database.greendao.TokenTxInfoDao;
import com.bupocket.database.greendao.TxDetailRespBoBeanDao;
import com.bupocket.database.greendao.TxInfoRespBoBeanDao;
import com.bupocket.database.greendao.VoucherDetailModelDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig addressBookListBeanDaoConfig;
    private final DaoConfig blockInfoRespBoBeanDaoConfig;
    private final DaoConfig imageInfoDaoConfig;
    private final DaoConfig logListModelDaoConfig;
    private final DaoConfig nodeBuildModelDaoConfig;
    private final DaoConfig superNodeModelDaoConfig;
    private final DaoConfig tokenTxInfoDaoConfig;
    private final DaoConfig txDetailRespBoBeanDaoConfig;
    private final DaoConfig txInfoRespBoBeanDaoConfig;
    private final DaoConfig voucherDetailModelDaoConfig;

    private final AddressBookListBeanDao addressBookListBeanDao;
    private final BlockInfoRespBoBeanDao blockInfoRespBoBeanDao;
    private final ImageInfoDao imageInfoDao;
    private final LogListModelDao logListModelDao;
    private final NodeBuildModelDao nodeBuildModelDao;
    private final SuperNodeModelDao superNodeModelDao;
    private final TokenTxInfoDao tokenTxInfoDao;
    private final TxDetailRespBoBeanDao txDetailRespBoBeanDao;
    private final TxInfoRespBoBeanDao txInfoRespBoBeanDao;
    private final VoucherDetailModelDao voucherDetailModelDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        addressBookListBeanDaoConfig = daoConfigMap.get(AddressBookListBeanDao.class).clone();
        addressBookListBeanDaoConfig.initIdentityScope(type);

        blockInfoRespBoBeanDaoConfig = daoConfigMap.get(BlockInfoRespBoBeanDao.class).clone();
        blockInfoRespBoBeanDaoConfig.initIdentityScope(type);

        imageInfoDaoConfig = daoConfigMap.get(ImageInfoDao.class).clone();
        imageInfoDaoConfig.initIdentityScope(type);

        logListModelDaoConfig = daoConfigMap.get(LogListModelDao.class).clone();
        logListModelDaoConfig.initIdentityScope(type);

        nodeBuildModelDaoConfig = daoConfigMap.get(NodeBuildModelDao.class).clone();
        nodeBuildModelDaoConfig.initIdentityScope(type);

        superNodeModelDaoConfig = daoConfigMap.get(SuperNodeModelDao.class).clone();
        superNodeModelDaoConfig.initIdentityScope(type);

        tokenTxInfoDaoConfig = daoConfigMap.get(TokenTxInfoDao.class).clone();
        tokenTxInfoDaoConfig.initIdentityScope(type);

        txDetailRespBoBeanDaoConfig = daoConfigMap.get(TxDetailRespBoBeanDao.class).clone();
        txDetailRespBoBeanDaoConfig.initIdentityScope(type);

        txInfoRespBoBeanDaoConfig = daoConfigMap.get(TxInfoRespBoBeanDao.class).clone();
        txInfoRespBoBeanDaoConfig.initIdentityScope(type);

        voucherDetailModelDaoConfig = daoConfigMap.get(VoucherDetailModelDao.class).clone();
        voucherDetailModelDaoConfig.initIdentityScope(type);

        addressBookListBeanDao = new AddressBookListBeanDao(addressBookListBeanDaoConfig, this);
        blockInfoRespBoBeanDao = new BlockInfoRespBoBeanDao(blockInfoRespBoBeanDaoConfig, this);
        imageInfoDao = new ImageInfoDao(imageInfoDaoConfig, this);
        logListModelDao = new LogListModelDao(logListModelDaoConfig, this);
        nodeBuildModelDao = new NodeBuildModelDao(nodeBuildModelDaoConfig, this);
        superNodeModelDao = new SuperNodeModelDao(superNodeModelDaoConfig, this);
        tokenTxInfoDao = new TokenTxInfoDao(tokenTxInfoDaoConfig, this);
        txDetailRespBoBeanDao = new TxDetailRespBoBeanDao(txDetailRespBoBeanDaoConfig, this);
        txInfoRespBoBeanDao = new TxInfoRespBoBeanDao(txInfoRespBoBeanDaoConfig, this);
        voucherDetailModelDao = new VoucherDetailModelDao(voucherDetailModelDaoConfig, this);

        registerDao(AddressBookListBean.class, addressBookListBeanDao);
        registerDao(BlockInfoRespBoBean.class, blockInfoRespBoBeanDao);
        registerDao(ImageInfo.class, imageInfoDao);
        registerDao(LogListModel.class, logListModelDao);
        registerDao(NodeBuildModel.class, nodeBuildModelDao);
        registerDao(SuperNodeModel.class, superNodeModelDao);
        registerDao(TokenTxInfo.class, tokenTxInfoDao);
        registerDao(TxDetailRespBoBean.class, txDetailRespBoBeanDao);
        registerDao(TxInfoRespBoBean.class, txInfoRespBoBeanDao);
        registerDao(VoucherDetailModel.class, voucherDetailModelDao);
    }
    
    public void clear() {
        addressBookListBeanDaoConfig.clearIdentityScope();
        blockInfoRespBoBeanDaoConfig.clearIdentityScope();
        imageInfoDaoConfig.clearIdentityScope();
        logListModelDaoConfig.clearIdentityScope();
        nodeBuildModelDaoConfig.clearIdentityScope();
        superNodeModelDaoConfig.clearIdentityScope();
        tokenTxInfoDaoConfig.clearIdentityScope();
        txDetailRespBoBeanDaoConfig.clearIdentityScope();
        txInfoRespBoBeanDaoConfig.clearIdentityScope();
        voucherDetailModelDaoConfig.clearIdentityScope();
    }

    public AddressBookListBeanDao getAddressBookListBeanDao() {
        return addressBookListBeanDao;
    }

    public BlockInfoRespBoBeanDao getBlockInfoRespBoBeanDao() {
        return blockInfoRespBoBeanDao;
    }

    public ImageInfoDao getImageInfoDao() {
        return imageInfoDao;
    }

    public LogListModelDao getLogListModelDao() {
        return logListModelDao;
    }

    public NodeBuildModelDao getNodeBuildModelDao() {
        return nodeBuildModelDao;
    }

    public SuperNodeModelDao getSuperNodeModelDao() {
        return superNodeModelDao;
    }

    public TokenTxInfoDao getTokenTxInfoDao() {
        return tokenTxInfoDao;
    }

    public TxDetailRespBoBeanDao getTxDetailRespBoBeanDao() {
        return txDetailRespBoBeanDao;
    }

    public TxInfoRespBoBeanDao getTxInfoRespBoBeanDao() {
        return txInfoRespBoBeanDao;
    }

    public VoucherDetailModelDao getVoucherDetailModelDao() {
        return voucherDetailModelDao;
    }

}
