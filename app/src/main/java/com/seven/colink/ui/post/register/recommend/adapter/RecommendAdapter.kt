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
    private val onChat: (String) -> Unit,
    private val onNext: () -> Unit,
    private val onProfile: (String) -> Unit,
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
                )
            )

            RecommendViewType.CARD -> CardViewHolder(
                ItemMemberCardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                inviteGroup,
                onChat,
                onProfile
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
                )
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
        private val binding: ItemRecommendTitleBinding
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Title
            tvRecommendTitleName.text = itemView.interBold()?.let {
                item.name.setFontType(root.context.getString(R.string.recommend_user_recommend_one),
                    it
                )
            }
        }
    }

    class CardViewHolder(
        private val binding: ItemMemberCardBinding,
        private val inviteGroup: (String) -> Unit,
        private val onChat: (String) -> Unit,
        private val onProfile: (String) -> Unit,
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Card
            ivMemberProfile.load(item.memberCard?.profileUrl)
            tvMemberName.text = item.memberCard?.name
            ivMemberLevelIcon.setLevelIcon(item.memberCard?.level ?: 0)
            tvMemberGrade.text = item.memberCard?.grade.toString()
            tvMemberLevelIcon.text = item.memberCard?.level.toString()
            tvMemberInfo.text = item.memberCard?.info

            val format = root.context.getString(R.string.recruit_project, item.memberCard?.recruits?: 0)
            val spannableString = SpannableString(format)

            val countString = "${item.memberCard?.recruits?: 0}개"
            val startIndex = format.indexOf(countString).let { if (it < 0) 14 else it }
            val endIndex = startIndex + countString.length

            if (startIndex != -1 && endIndex <= spannableString.length) {
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
            } else {
                // startIndex가 -1이거나 endIndex가 범위를 벗어나는 경우에 대한 처리
                // 여기에 적절한 로직을 추가하거나, 로그를 남겨 문제를 추적할 수 있습니다.
            }
            root.setOnClickListener {
                item.memberCard?.key?.let(onProfile)
            }

            tvMemberRecruitProject.text = spannableString
            btMemberInviteGroup.setOnClickListener {
                item.memberCard?.key?.let { key -> inviteGroup(key) }
            }
            btMemberChat.setOnClickListener {
                item.memberCard?.key?.let { key -> onChat(key) }
            }
        }
    }

    class MiddleViewHolder(
        private val binding: ItemRecommendMiddleBinding
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Middle
            tvRecommendSend.text = itemView.interBold()?.let {
                item.memberName.setFontType(root.context.getString(R.string.recommend_send_message),
                    it
                )
            }
            tvRecommendOther.text = itemView.interBold()?.let {
                item.memberName.setFontType(root.context.getString(R.string.recommend_other_member),
                    it
                )
            }
        }
    }

    class OthersViewHolder(
        private val binding: ItemPostMemberInfoBinding
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            item as RecommendType.Others
            ivUser.load(item.memberInfo?.profileUrl)
            tvUserName.text = item.memberInfo?.name
            tvUserIntroduction.text = item.memberInfo?.info.toString()
            tvUserGrade.text = item.memberInfo?.grade.toString()
            ivLevelDiaIcon.setLevelIcon(item.memberInfo?.level?: 0)
            tvLevelDiaIcon.text = item.memberInfo?.level.toString()
        }
    }


    class CloseViewHolder(
        private val binding: ItemButtonBinding,
        private val onClose: () -> Unit,
    ) : RecommendViewHolder(binding.root) {
        override fun onBind(item: RecommendType) = with(binding) {
            btItemButton.setOnClickListener {
                onClose
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
