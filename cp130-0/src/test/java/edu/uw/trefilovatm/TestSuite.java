package edu.uw.trefilovatm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AccountManagerTest;
import test.AccountTest;
import test.DaoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccountTest.class, AccountManagerTest.class, DaoTest.class})
public class TestSuite{
}