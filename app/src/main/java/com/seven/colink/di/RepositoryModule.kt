package com.seven.colink.di

import com.seven.colink.data.firebase.repository.AuthRepositoryImpl
import com.seven.colink.data.firebase.repository.ChatRepositoryImpl
import com.seven.colink.data.firebase.repository.CommentRepositoryImpl
import com.seven.colink.data.firebase.repository.GroupRepositoryImpl
import com.seven.colink.data.firebase.repository.ImageRepositoryImpl
import com.seven.colink.data.firebase.repository.NotificationStoreRepositoryImpl
import com.seven.colink.data.remote.repository.NotifyRepositoryImpl
import com.seven.colink.data.firebase.repository.PostRepositoryImpl
import com.seven.colink.data.firebase.repository.ProductRepositoryImpl
import com.seven.colink.data.firebase.repository.RecruitRepositoryImpl
import com.seven.colink.data.firebase.repository.ScheduleRepositoryImpl
import com.seven.colink.data.firebase.repository.UserRepositoryImpl
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.domain.repository.CommentRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.NotificationStoreRepository
import com.seven.colink.domain.repository.NotifyRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.domain.repository.ScheduleRepository
import com.seven.colink.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository
    @Binds
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository
    @Binds
    abstract fun bindChatRepository(repository: ChatRepositoryImpl): ChatRepository
    @Binds
    abstract fun bindCommentRepository(repository: CommentRepositoryImpl): CommentRepository
    @Binds
    abstract fun bindGroupRepository(repository: GroupRepositoryImpl): GroupRepository
    @Binds
    abstract fun bindImageRepository(repository: ImageRepositoryImpl): ImageRepository
    @Binds
    abstract fun bindPostRepository(repository: PostRepositoryImpl): PostRepository
    @Binds
    abstract fun bindProductRepository(repository: ProductRepositoryImpl): ProductRepository
    @Binds
    abstract fun bindRecruitRepository(repository: RecruitRepositoryImpl): RecruitRepository
    @Binds
    abstract fun bindScheduleRepository(repository: ScheduleRepositoryImpl): ScheduleRepository
    @Binds
    abstract fun bindNotificationStoreRepository(repository: NotificationStoreRepositoryImpl): NotificationStoreRepository
    @Binds
    abstract fun bindNotifyRepository(repository: NotifyRepositoryImpl): NotifyRepository
}