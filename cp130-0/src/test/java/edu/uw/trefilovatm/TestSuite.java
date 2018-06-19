package edu.uw.trefilovatm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AccountManagerTest;
import test.AccountTest;
import test.BrokerTest;
import test.DaoTest;
import test.PrivateMessageCodecTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccountTest.class, AccountManagerTest.class, DaoTest.class, BrokerTest.class, PrivateMessageCodecTest.class})
public class TestSuite{
}