package com.srm325.gobble.ui.features.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.srm325.gobble.R
import com.srm325.gobble.core.makeItGone
import com.srm325.gobble.core.makeItVisible
import com.srm325.gobble.data.model.Post
import com.bumptech.glide.Glide
import timber.log.Timber
import java.lang.System.load

class PostAdapter(var postList: List<Post>, val callback: FeedFragmentCallback) : RecyclerView.Adapter<PostViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.single_post_item, parent, false)
        return PostViewHolder(view)

    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        Timber.d(postList[position].image)

        Glide
            .with(context)
            .load(postList[position].image)
            .dontAnimate()
            .placeholder(R.drawable.ic_loading)
            .into(holder.postImage)

        Glide
            .with(context)
            .load(postList[position].user.image)
            .dontAnimate()
            .placeholder(R.drawable.ic_loading)
            .into(holder.profileImage)

        holder.userName.text = postList[position].user.userName
        holder.description.text = postList[position].description
        holder.postContainer.setOnClickListener {
            val postId =  postList[position].id
            val action = if(postList[position].winner.isEmpty()) {
                FeedFragmentDirections.actionFeedFragmentToPostDetailsFragment(postId)
            }else{
                FeedFragmentDirections.actionFeedFragmentToWinnerFragment(postId)
            }
            it.findNavController().navigate(action)
        }

        if(callback.checkCurrentUser(postList[position].user.email)) {
            holder.iWantThisButton.visibility = View.GONE
            holder.cancelButton.visibility = View.GONE
        }else {
            if(postList[position].winner.isEmpty()) {
                if (callback.getCurrentUserEmail() in postList[position].claimers) {
                    holder.iWantThisButton.visibility = View.GONE
                    holder.cancelButton.visibility = View.VISIBLE
                } else {
                    holder.iWantThisButton.visibility = View.VISIBLE
                    holder.cancelButton.visibility = View.GONE
                }
            }

        }

        holder.iWantThisButton.setOnClickListener {
            val x = holder.iWantThisButton.visibility
            holder.iWantThisButton.visibility = holder.cancelButton.visibility
            holder.cancelButton.visibility = x
            callback.onIWantThisClicked(postList[position].id)
        }

        holder.cancelButton.setOnClickListener {
            val x = holder.iWantThisButton.visibility
            holder.iWantThisButton.visibility = holder.cancelButton.visibility
            holder.cancelButton.visibility = x
            callback.onCancelClaimClicked(postList[position].id)
        }
        if(postList[position].image.isEmpty())
            holder.postImage.makeItGone()
        else
            holder.postImage.makeItVisible()
        if(postList[position].description.isEmpty())
            holder.description.makeItGone()
        else
            holder.description.makeItVisible()

    }

    override fun getItemCount() = postList.size
}