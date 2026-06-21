package com.dakkho.android.presentation.screens.privacypolicy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "গোপনীয়তা নীতি",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DesignToken.Spacing.md)
        ) {
            Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))

            // Last updated info
            GlassCard(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "সর্বশেষ আপডেট: ১ জানুয়ারি, ২০২৫",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(DesignToken.Spacing.md)
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Section 1: তথ্য সংগ্রহ
            PrivacySection(
                sectionNumber = "১",
                title = "তথ্য সংগ্রহ",
                paragraphs = listOf(
                    "দাক্ষ আপনার ব্যক্তিগত তথ্য সংগ্রহ করে আপনার শিক্ষার অভিজ্ঞতা উন্নত করতে। আমরা সংগ্রহ করি: আপনার নাম, ইমেইল ঠিকানা, ফোন নম্বর, শিক্ষাপ্রতিষ্ঠানের নাম, এবং শিক্ষার্থী আইডি। এই তথ্য অ্যাকাউন্ট তৈরি ও পরিচালনার জন্য প্রয়োজন।",
                    "আমরা স্বয়ংক্রিয়ভাবে ডিভাইস তথ্য সংগ্রহ করি, যার মধ্যে রয়েছে ডিভাইসের ধরন, অপারেটিং সিস্টেম, অ্যাপ সংস্করণ, এবং ক্র্যাশ রিপোর্ট। এছাড়া আমরা ব্যবহারের পরিসংখ্যান সংগ্রহ করি যেমন কোন কোর্স দেখেছেন, কত সময় ব্যয় করেছেন, এবং আপনার অগ্রগতির তথ্য।",
                    "আপনার অবস্থানের তথ্য আমরা সংগ্রহ করি না যদি না আপনি স্পষ্টভাবে অনুমতি দেন। ক্যামেরা ও মাইক্রোফোনের প্রবেশাধিকার কেবল আপনার অনুমতির ভিত্তিতে এবং শুধুমাত্র সংশ্লিষ্ট ফিচার ব্যবহারের সময় চাওয়া হবে।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Section 2: তথ্য ব্যবহার
            PrivacySection(
                sectionNumber = "২",
                title = "তথ্য ব্যবহার",
                paragraphs = listOf(
                    "আমরা আপনার তথ্য ব্যবহার করি সেবা প্রদান ও উন্নত করতে, ব্যক্তিগতকৃত শিক্ষার অভিজ্ঞতা তৈরি করতে, এবং আপনার সাথে যোগাযোগ রক্ষা করতে। আপনার শিক্ষার অগ্রগতির তথ্য ব্যবহার করে আমরা কাস্টম স্টাডি প্ল্যান ও সুপারিশ প্রদান করি।",
                    "আপনার ব্যবহারের ডেটা বিশ্লেষণ করে আমরা প্ল্যাটফর্মের পারফরম্যান্স উন্নত করি, নতুন ফিচার তৈরি করি, এবং বাগ সংশোধন করি। এই ডেটা সমষ্টিগত ও বেনামী রূপে বিশ্লেষণ করা হয় যাতে আপনার পরিচয় সুরক্ষিত থাকে।",
                    "আমরা আপনার তথ্য কোনো তৃতীয় পক্ষের কাছে বিক্রি করি না। আপনার তথ্য শুধুমাত্র এই নীতিতে বর্ণিত উদ্দেশ্যে ব্যবহার করা হবে।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Section 3: তথ্য সুরক্ষা
            PrivacySection(
                sectionNumber = "৩",
                title = "তথ্য সুরক্ষা",
                paragraphs = listOf(
                    "আমরা শিল্পের মান অনুযায়ী এনক্রিপশন প্রযুক্তি ব্যবহার করে আপনার তথ্য সুরক্ষিত রাখি। সকল ডেটা ট্রান্সমিশন AES-256 এনক্রিপশনের মাধ্যমে সুরক্ষিত এবং সার্ভারে সংরক্ষিত ডেটা এনক্রিপ্টেড অবস্থায় থাকে।",
                    "আমাদের টিম নিয়মিত নিরাপত্তা অডিট ও পেনিট্রেশন টেস্ট পরিচালনা করে। অননুমোদিত প্রবেশের প্রচেষ্টা সনাক্ত করতে আমরা অ্যাডভান্সড মনিটরিং সিস্টেম ব্যবহার করি। ডেটা ব্রিচ হলে আমরা ৭২ ঘণ্টার মধ্যে আপনাকে অবহিত করব।",
                    "আপনার অ্যাকাউন্টের নিরাপত্তার জন্য আমরা দুই-ধাপ যাচাইকরণ (2FA) সুপারিশ করি। আপনার পাসওয়ার্ড হ্যাশড ও সল্টেড অবস্থায় সংরক্ষিত থাকে, যা আমরা বা কেউই পড়তে পারি না।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Section 4: কুকিজ
            PrivacySection(
                sectionNumber = "৪",
                title = "কুকিজ",
                paragraphs = listOf(
                    "দাক্ষ অ্যাপ ও ওয়েবসাইটে কুকিজ ব্যবহার করে আপনার অভিজ্ঞতা উন্নত করতে। অপরিহার্য কুকিজ আপনার লগইন সেশন বজায় রাখতে ও নিরাপত্তা নিশ্চিত করতে প্রয়োজন। বিশ্লেষণমূলক কুকিজ আমাদের ব্যবহারের ধরণ বুঝতে সাহায্য করে।",
                    "আপনি অ-অপরিহার্য কুকিজ অক্ষম করতে পারেন অ্যাপের সেটিংস থেকে। কুকিজ অক্ষম করলে কিছু ফিচার সঠিকভাবে কাজ না করতে পারে। আমরা কুকিজের মাধ্যমে ব্যক্তিগত তথ্য সংগ্রহ করি না যা আমরা অন্যথায় সংগ্রহ করি না।",
                    "কুকিজ আপনার ডিভাইসে সংরক্ষিত থাকে এবং আপনি যেকোনো সময় আপনার ব্রাউজার বা অ্যাপ সেটিংস থেকে মুছে ফেলতে পারেন।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Section 5: তৃতীয় পক্ষ
            PrivacySection(
                sectionNumber = "৫",
                title = "তৃতীয় পক্ষ",
                paragraphs = listOf(
                    "আমরা কিছু তৃতীয় পক্ষ সেবা ব্যবহার করি যেমন পেমেন্ট প্রসেসর (বিকাশ, নগদ), অ্যানালিটিক্স (Firebase), এবং ক্লাউড স্টোরেজ (AWS)। এই সেবাগুলোর নিজস্ব গোপনীয়তা নীতি রয়েছে এবং তারা আপনার তথ্য আমাদের নীতির সাথে সামঞ্জস্য রেখে ব্যবহার করে।",
                    "আমরা তৃতীয় পক্ষের সাথে আপনার ব্যক্তিগত তথ্য শেয়ার করি না, তবে আইনি বাধ্যবাধকতা, নিরাপত্তা প্রয়োজনীয়তা, বা আপনার সম্মতির ভিত্তিতে তথ্য প্রদান করতে পারি। সরকারি সংস্থার আইনি অনুরোধের ক্ষেত্রে আমরা প্রযোজ্য আইন মেনে চলব।",
                    "আমরা আমাদের তৃতীয় পক্ষ সেবাদাতাদের নিয়মিত নিরাপত্তা মূল্যায়ন করি এবং তারা যেন আমাদের গোপনীয়তা মানদণ্ড পূরণ করে তা নিশ্চিত করি। কোনো তৃতীয় পক্ষের নীতিতে পরিবর্তন হলে আমরা যথাসম্ভব দ্রুত আপনাকে অবহিত করব।"
                )
            )

            Spacer(modifier = Modifier.height(DesignToken.Spacing.xl))
        }
    }
}

@Composable
private fun PrivacySection(
    sectionNumber: String,
    title: String,
    paragraphs: List<String>
) {
    GlassCard(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(DesignToken.Spacing.md)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SkyBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sectionNumber,
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(DesignToken.Spacing.sm))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Spacing.md))

            // Paragraphs
            paragraphs.forEachIndexed { index, paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
                if (index < paragraphs.lastIndex) {
                    Spacer(modifier = Modifier.height(DesignToken.Spacing.sm))
                }
            }
        }
    }
}
