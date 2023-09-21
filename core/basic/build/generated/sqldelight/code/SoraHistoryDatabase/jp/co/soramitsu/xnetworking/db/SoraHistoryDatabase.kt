package jp.co.soramitsu.xnetworking.db

import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver
import jp.co.soramitsu.xnetworking.db.basic.newInstance
import jp.co.soramitsu.xnetworking.db.basic.schema

public interface SoraHistoryDatabase : Transacter {
  public val soraHistoryDatabaseQueries: SoraHistoryDatabaseQueries

  public companion object {
    public val Schema: SqlDriver.Schema
      get() = SoraHistoryDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): SoraHistoryDatabase =
        SoraHistoryDatabase::class.newInstance(driver)
  }
}
