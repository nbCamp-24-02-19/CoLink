package com.seven.colink.ui.post.register.recommend.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemButtonBinding
import com.seven.colink.databinding.ItemMemberCardBinding
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemRecommendMiddleBinding
import com.seven.colink.databinding.ItemRecommendTitleBinding
import com.seven.colink.databinding.UnknownItemBinding
import com.seven.colink.ui.post.register.recommend.type.RecommendViewType
import com.seven.colink.ui.post.register.recommend.type.RecommendType
import com.seven.colink.util.interBold
import com.seven.colink.util.setFontType
import com.seven.colink.util.setLevelIcon

class RecommendAdapter(
    private val inviteGroup: (String) -> Unit,
    private val editGroup: (String) -> Unit,
    private val onChat: (String) -> Unit,
    private val onNext: () -> Unit,
    private val onDetail: (String) -> Unit,
) : ListAdapter<RecommendType, RecommendAdapter.RecommendViewHolder>(
    object : DiffUtil.ItemCallback<RecommendType>() {
        override fun areItemsTheSame(
            oldItem: RecommendType,
            newItem: RecommendType
        ) = when {
            oldItem is RecommendType.Card && newItem is RecommendType.Card -> {
                oldItem.memberCard?.key == newItem.memberCard?.key
            }

            oldItem is RecommendType.Others && newItem is RecommendType.Others -> {
                oldItem.memberInfo?.key == newItem.memberInfo?.key
            }

            else -> true
        }

        override fun areContentsTheSame(
            oldItem: RecommendType,
            newItem: RecommendType
        ) = oldItem == newItem

    }
) {
    abstract class RecommendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: RecommendType)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is RecommendType.Title -> RecommendViewType.TITLE
        is RecommendType.Card -> RecommendViewType.CARD
        is RecommendType.Middle -> RecommendViewType.MIDDLE
        is RecommendType.Others -> RecommendViewType.OTHERS
        is RecommendType.Close -> RecommendViewType.CLOSE
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (RecommendViewType.from(viewType)) {
            RecommendViewType.TITLE -> TitleViewHolder(
                ItemRecommendTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                editGroup
            )

            RecommendViewType.CARD -> CardViewHolder(
                ItemMemberCardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                inviteGroup,
                onChat,
                onDetail
            )

            RecommendViewType.MIDDLE -> MiddleViewHolder(
                ItemRecommendMiddleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            RecommendViewType.OTHERS -> OthersViewHolder(
                ItemPostMemberInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onDetail
            )

            RecommendViewType.CLOSE -> CloseViewHolder(
                ItemButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onNext
            )

            else -> UnknownViewHolder(
                UnknownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    class TitleViewHolder(
        private val binding: ItemRecommendTitleBinding,
        private val editGroup: (String) -> Unit
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Title
            tvRecommendTitleName.text = item.name.setFontType(root.context.getString(R.string.recommend_user_recommend_one), itemView.interBold())
            tvRecommendEditGroup.setOnClickListener {
                editGroup(item.key)
            }
        }
    }

    class CardViewHolder(
        private val binding: ItemMemberCardBinding,
        private val inviteGroup: (String) -> Unit,
        private val onChat: (String) -> Unit,
        private val onDetail: (String) -> Unit,
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Card
            ivMemberProfile.load(item.memberCard?.profileUrl)
            ivMemberLevelIcon.setLevelIcon(item.memberCard?.level ?: 0)
            tvMemberName.text = item.memberCard?.name
            tvMemberGrade.text = item.memberCard?.grade.toString()
            tvMemberLevelIcon.text = item.memberCard?.level.toString()
            tvMemberInfo.text = item.memberCard?.info

            val format = root.context.getString(R.string.recruit_project, item.memberCard?.recruits ?: 0)
            val spannableString = SpannableString(format)

            val countString = "${item.memberCard?.recruits ?: 0} 개"
            val startIndex = format.indexOf(countString)
            val endIndex = startIndex + countString.length

            spannableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(root.context, R.color.main_color)),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableString.setSpan(
                RelativeSizeSpan(1.2f),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvMemberRecruitProject.text = spannableString
            btMemberInviteGroup.setOnClickListener {
                item.memberCard?.key?.let { key -> inviteGroup(key) }
            }
            btMemberChat.setOnClickListener {
                item.memberCard?.key?.let { key -> onChat(key) }
            }
            root.setOnClickListener {
                item.memberCard?.key?.let { it1 -> onDetail(it1) }
            }
        }
    }

    class MiddleViewHolder(
        private val binding: ItemRecommendMiddleBinding
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Middle
            tvRecommendSend.text = item.memberName.setFontType(root.context.getString(R.string.recommend_send_message), itemView.interBold())
            tvRecommendOther.text = item.memberName.setFontType(root.context.getString(R.string.recommend_other_member), itemView.interBold())
        }
    }

    class OthersViewHolder(
        private val binding: ItemPostMemberInfoBinding,
        private val onDetail: (String) -> Unit,
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Others
            ivUser.load(item.memberInfo?.profileUrl)
            tvUserName.text = item.memberInfo?.name
            tvUserIntroduction.text = item.memberInfo?.info
            tvUserGrade.text = item.memberInfo?.grade.toString()
            ivLevelDiaIcon.setLevelIcon(item.memberInfo?.level?: 0)
            tvLevelDiaIcon.text = item.memberInfo?.level.toString()

            root.setOnClickListener {
                item.memberInfo?.key?.let { onDetail(it) }
            }
        }
    }


    class CloseViewHolder(
        private val binding: ItemButtonBinding,
        private val onNext: () -> Unit,
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            btItemButton.setOnClickListener {
                onNext()
            }
        }

    }

    class UnknownViewHolder(
        private val binding: UnknownItemBinding
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {

        }

    }

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
