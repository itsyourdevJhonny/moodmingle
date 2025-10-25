package com.emc.moodmingle.ui.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.screens.Post
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor

@Composable
fun MultimediaCard(composable: @Composable () -> Unit, post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = BrushPrimaryGradient,
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(post.avatar)

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = post.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = post.timeAgo,
                        fontSize = 12.sp,
                        color = GrayTextColor
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MoreAction()

                Surface(
                    color = Color(0xFFF0E6FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "😄 Happy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF4A00E0)
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 10.dp)
                .fillMaxWidth(),
            text = "Test description",
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = Color.White
        )

        composable()

        Spacer(Modifier.height(10.dp))

        PostActions(post)

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun PostActions(post: Post) {
    val globalLikesState = remember { mutableIntStateOf(post.likes) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        ActionContainer({ CommentAction(post.comments) }, post.comments)
        ActionContainer({ ReactionAction(globalLikesState) }, globalLikesState.intValue)
        ActionContainer({ TextAction(R.drawable.share, "Share") }, post.shares)
    }
}

@Composable
fun ActionContainer(action: @Composable () -> Unit, numbers: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        action()

        Text(text = "$numbers", color = GrayTextColor)
    }
}

@Composable
fun AvatarImage(@DrawableRes avatarRes: Int) {
    AsyncImage(
        model = avatarRes,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f)),
        contentScale = ContentScale.Crop
    )
}