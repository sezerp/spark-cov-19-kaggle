package com.pawelzabczynski.infrastructure

import com.pawelzabczynski.config.Sensitive

case class DbConfig(username: String, password: Sensitive, url: String, migrateOnStart: Boolean, driver: String, connectThreadPoolSize: Int)
