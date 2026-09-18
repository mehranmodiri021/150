package com.example.data.repository

import com.example.domain.model.ChallengeItem
import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.LeaderboardType
import com.example.domain.model.UserProfile

/**
 * ONLINE BACKEND ARCHITECTURE CONTRACTS (Clean Remote Data Source)
 */
interface RemoteChallengeDataSource {
    suspend fun fetchActiveChallenges(): Result<List<ChallengeItem>>
    suspend fun submitChallengeScore(challengeId: String, score: Int, completionTimeSeconds: Int): Result<Boolean>
}

interface RemoteLeaderboardDataSource {
    suspend fun fetchLeaderboard(type: LeaderboardType, page: Int = 1, pageSize: Int = 50): Result<List<LeaderboardEntry>>
    suspend fun submitScore(score: Int, leaderboardType: LeaderboardType): Result<LeaderboardEntry>
}

interface RemoteUserSyncDataSource {
    suspend fun syncProfile(localProfile: UserProfile): Result<UserProfile>
    suspend fun verifyPurchase(purchaseToken: String, productId: String): Result<Boolean>
}

class DefaultRemoteDataSourceStub : RemoteChallengeDataSource, RemoteLeaderboardDataSource, RemoteUserSyncDataSource {
    override suspend fun fetchActiveChallenges(): Result<List<ChallengeItem>> {
        return Result.success(emptyList())
    }

    override suspend fun submitChallengeScore(challengeId: String, score: Int, completionTimeSeconds: Int): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun fetchLeaderboard(type: LeaderboardType, page: Int, pageSize: Int): Result<List<LeaderboardEntry>> {
        return Result.success(emptyList())
    }

    override suspend fun submitScore(score: Int, leaderboardType: LeaderboardType): Result<LeaderboardEntry> {
        return Result.failure(NotImplementedError("Offline mode: Real backend server not configured."))
    }

    override suspend fun syncProfile(localProfile: UserProfile): Result<UserProfile> {
        return Result.success(localProfile)
    }

    override suspend fun verifyPurchase(purchaseToken: String, productId: String): Result<Boolean> {
        return Result.success(true)
    }
}
