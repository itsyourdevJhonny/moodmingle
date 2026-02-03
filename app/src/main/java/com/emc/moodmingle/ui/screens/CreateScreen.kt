package com.emc.moodmingle.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGradientBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

private data class PostType(
    @DrawableRes val imageIcon: Int,
    val description: String
)

@Composable
fun CreateScreen(onCreatePost: () -> Unit, onCreateDailyMood: () -> Unit, onBack: () -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val currentUser by userViewModel.loggedUser

    val types = listOf(
        R.drawable.create_post_colored to "Post",
        R.drawable.daily_colored to "Daily Mood",
        R.drawable.poll_colored to "Poll",
        R.drawable.survey_colored to "Survey"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 38.dp, bottom = 42.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BackIcon(onClick = onBack)

                    Text(
                        text = "Share what you feel",
                        style = Typography.bodyLarge.copy(color = Color.White)
                    )
                }

                AsyncImage(
                    model = currentUser?.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .gradientCircleBorder(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                types.forEach { (imageIcon, label) ->
                    TypeItem(imageIcon, label) {
                        when (label) {
                            "Post" -> onCreatePost()
                            "Daily Mood" -> onCreateDailyMood()
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                listOf(
                    Icons.Default.Settings to "Settings",
                    Icons.AutoMirrored.Filled.List to "Archived"
                ).forEach { (icon, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.drawGradient()
                        )

                        Text(
                            text = label,
                            style = Typography.bodyMedium.copy(
                                color = GrayTextColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 16.dp))

            Templates()
        }
    }

    /*val core = Post(
        label = "Core",
        types = listOf(
            PostType(
                R.drawable.create_post_colored,
                "Post"
            ),
            PostType(
                R.drawable.survey_colored, "Survey"
            ),
            PostType(
                R.drawable.poll_colored, "Poll"
            ),
            PostType(
                R.drawable.daily_colored, "Daily Mood"
            )
        )
    )

    val discussionAndOpinion = Post(
        label = "Discussion and Opinion",
        types = listOf(
            PostType(
                R.drawable.create_post_colored,
                "Question"
            ),
            PostType(
                R.drawable.survey_colored,
                "Hot Take"
            ),
            PostType(
                R.drawable.poll_colored,
                "Debate"
            ),
            PostType(
                R.drawable.daily_colored,
                "Advice Request"
            )
        )
    )

    val emotionalAndWellBeing = Post(
        label = "Emotional and Well-being",
        types = listOf(
            PostType(
                R.drawable.create_post_colored,
                "Rant"
            ),
            PostType(
                R.drawable.survey_colored,
                "Gratitude"
            ),
            PostType(
                R.drawable.poll_colored,
                "Win of the day"
            ),
            PostType(
                R.drawable.daily_colored,
                "Support Request"
            )
        )
    )

    val engagementAndInteraction = Post(
        label = "Engagement and Interaction",
        types = listOf(
            PostType(
                R.drawable.create_post_colored,
                "Challenge"
            ),
            PostType(
                R.drawable.survey_colored,
                "AMA (Ask Me Anything)"
            ),
            PostType(
                R.drawable.poll_colored,
                "This or That"
            ),
            PostType(
                R.drawable.daily_colored,
                "Rate This"
            )
        )
    )


    Box(modifier = Modifier.fillMaxSize().background(PrimaryDark), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = core.label,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Item(core.types[0].imageIcon, core.types[0].description)
                    Item(core.types[1].imageIcon, core.types[1].description)

                    Item(core.types[2].imageIcon, core.types[2].description)
                    Item(core.types[3].imageIcon, core.types[3].description)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = discussionAndOpinion.label,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Item(
                        discussionAndOpinion.types[0].imageIcon,
                        discussionAndOpinion.types[0].description
                    )
                    Item(
                        discussionAndOpinion.types[1].imageIcon,
                        discussionAndOpinion.types[1].description
                    )

                    Item(
                        discussionAndOpinion.types[2].imageIcon,
                        discussionAndOpinion.types[2].description
                    )
                    Item(
                        discussionAndOpinion.types[3].imageIcon,
                        discussionAndOpinion.types[3].description
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = emotionalAndWellBeing.label,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Item(
                        emotionalAndWellBeing.types[0].imageIcon,
                        emotionalAndWellBeing.types[0].description
                    )
                    Item(
                        emotionalAndWellBeing.types[1].imageIcon,
                        emotionalAndWellBeing.types[1].description
                    )

                    Item(
                        emotionalAndWellBeing.types[2].imageIcon,
                        emotionalAndWellBeing.types[2].description
                    )
                    Item(
                        emotionalAndWellBeing.types[3].imageIcon,
                        emotionalAndWellBeing.types[3].description
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = engagementAndInteraction.label,
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Item(
                        engagementAndInteraction.types[0].imageIcon,
                        engagementAndInteraction.types[0].description
                    )
                    Item(
                        engagementAndInteraction.types[1].imageIcon,
                        engagementAndInteraction.types[1].description
                    )

                    Item(
                        engagementAndInteraction.types[2].imageIcon,
                        engagementAndInteraction.types[2].description
                    )
                    Item(
                        engagementAndInteraction.types[3].imageIcon,
                        engagementAndInteraction.types[3].description
                    )
                }
            }
        }
    }*/
}

@Composable
fun Templates() {
    val templates = listOf(
        "Post" to listOf(
            R.raw.post_template_1,
            R.raw.post_template_2,
            R.raw.post_template_3,
            R.raw.post_template_4,
        ),
        "Daily Mood" to listOf(
            R.drawable.city_walk,
            R.drawable.beach_vibes,
            R.drawable.happy_person,
            R.drawable.sunset_chill,
            R.drawable.mountain_hike
        ),
        "Pool" to listOf(
            R.drawable.city_walk,
            R.drawable.beach_vibes,
            R.drawable.happy_person,
            R.drawable.sunset_chill,
            R.drawable.mountain_hike
        ),
        "Survey" to listOf(
            R.drawable.city_walk,
            R.drawable.beach_vibes,
            R.drawable.happy_person,
            R.drawable.sunset_chill,
            R.drawable.mountain_hike
        )
    )

    Text(
        text = "Templates",
        fontSize = 20.sp,
        color = Color.White,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        templates.forEach { (label, imageList) ->
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = label,
                        color = Color.White
                    )

                    HorizontalDivider(thickness = 0.5.dp)
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(imageList.shuffled()) { index, image ->
                        Image(
                            painter = painterResource(image),
                            contentDescription = label,
                            modifier = Modifier
                                .height(if (label == "Post") 130.dp else 220.dp)
                                .width(if (label == "Post") 250.dp else 150.dp)
                                .padding(
                                    start = if (index == 0) 8.dp else 0.dp,
                                    end = if (index == imageList.size - 1) 8.dp else 0.dp
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .roundedGradientBorder(16.dp)
                                .clickable {},
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeItem(imageIcon: Int, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(SecondaryDark, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(imageIcon),
                contentDescription = label,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Crop
            )

            Text(text = label, color = Color.White, fontSize = 16.sp)
        }
    }
}

fun getPostTemplates(index: Int): Color {
    val templatesMap = mapOf(
        0 to 0xFFFF5634,
        1 to 0xFFFF1815,
        2 to 0xFF95FF10,
        3 to 0xFF2AFFEE
    )

    return Color(templatesMap[index]!!)
}