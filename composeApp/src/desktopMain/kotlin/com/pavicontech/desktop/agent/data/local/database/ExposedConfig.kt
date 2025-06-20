package com.pavicontech.desktop.agent.data.local.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction



object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

fun main(){
    Database.connect("jdbc:sqlite:sample.db", driver = "org.sqlite.JDBC")

    // Define a simple table


    // Create and insert data
    transaction {
        SchemaUtils.create(Users)

        Users.insert {
            it[name] = "Alice"
        }

        Users.selectAll().forEach {
            println("${it[Users.id]}: ${it[Users.name]}")
        }
    }
}