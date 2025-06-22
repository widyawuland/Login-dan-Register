package com.widya.loginregisteruts

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var userDao: UserDao
    private lateinit var appDatabase: AppDatabase

    private val user1 = User(
        id = 1,
        name = "Anton",
        email = "anton@gmail.com",
        password = "anton123",
        phone = "08123456789",
        address = "jl. serayu"
    )

    private val user2 = User(
        id = 2,
        name = "Ayu",
        email = "ayu@gmail.com",
        password = "ayu12345",
        phone = "08987654321",
        address = "purworejo 2"
    )

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = appDatabase.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDatabase.close()
    }

    private suspend fun insertUserData(vararg users: User) {
        for (user in users) {
            userDao.insert(user)
        }
    }

    @Test
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        insertUserData(user1)
        val allItems = userDao.getAllUsers().first()
        Assert.assertEquals(user1, allItems[0])
    }

    @Test
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        insertUserData(user1, user2)
        val allItems = userDao.getAllUsers().first()
        Assert.assertEquals(user1, allItems[0])
        Assert.assertEquals(user2, allItems[1])
    }

    @Test
    fun daoGetUser_returnsUserFromDB() = runBlocking {
        insertUserData(user1)
        val user = userDao.getUser(user1.id).first()
        Assert.assertEquals(user1, user)
    }

    @Test
    fun daoDeleteUsers_deletesAllUsersFromDB() = runBlocking {
        insertUserData(user1, user2)
        userDao.delete(user1)
        userDao.delete(user2)
        val allUsers = userDao.getAllUsers().first()
        Assert.assertTrue(allUsers.isEmpty())
    }

    @Test
    fun daoUpdateUsers_updatesUsersInDB() = runBlocking {
        insertUserData(user1, user2)

        val updatedUser1 = user1.copy(
            email = "antonupdate@gmail.com",
            password = "antonUpdate123",
            address = "1234567890"
        )
        val updatedUser2 = user2.copy(
            email = "ayuupdate@gmail.com",
            password = "ayuUpdate098",
            address = "0987654321"
        )

        userDao.update(updatedUser1)
        userDao.update(updatedUser2)

        val allUsers = userDao.getAllUsers().first()
        Assert.assertEquals(updatedUser1, allUsers[0])
        Assert.assertEquals(updatedUser2, allUsers[1])
    }
}