package com.wecan.voucher.management;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
        "com.wecan.voucher.management.security",
        "com.wecan.voucher.management.voucherSystem"
})
public class AllTests {
}