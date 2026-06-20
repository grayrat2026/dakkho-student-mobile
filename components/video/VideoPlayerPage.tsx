import React, { useState, useRef, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Dimensions } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { Play, Pause, Maximize, Minimize, SkipBack, SkipForward, Settings, ChevronLeft, Subtitles, RotateCcw } from 'lucide-react-native';
import { useNavigationStore } from '@/lib/store';
import { LightColors } from '@/lib/theme';

const { width, height } = Dimensions.get('window');

export function VideoPlayerPage() {
  const navigate = useNavigationStore((s) => s.navigate);
  const goBack = useNavigationStore((s) => s.goBack);
  const [isPlaying, setIsPlaying] = useState(false);
  const [showControls, setShowControls] = useState(true);
  const [speed, setSpeed] = useState(1);
  const [progress, setProgress] = useState(0.35);
  const [currentTime, setCurrentTime] = useState(750); // seconds
  const [duration, setDuration] = useState(2100); // seconds
  const [isFullscreen, setIsFullscreen] = useState(false);
  const colors = LightColors;

  const speeds = [0.5, 0.75, 1, 1.25, 1.5, 2];

  // Auto-hide controls after 3 seconds
  useEffect(() => {
    if (showControls && isPlaying) {
      const timer = setTimeout(() => setShowControls(false), 3000);
      return () => clearTimeout(timer);
    }
  }, [showControls, isPlaying]);

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
  };

  // Seek bar handler
  const handleSeekBar = (event: any) => {
    // Placeholder — in production, use the native event to calculate position
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        {/* Video Area */}
        <TouchableOpacity
          style={styles.videoArea}
          activeOpacity={1}
          onPress={() => setShowControls(!showControls)}
        >
          <View style={styles.videoPlaceholder}>
            <Text style={styles.videoPlaceholderText}>DAKKHO</Text>
            <Text style={styles.videoPlaceholderSub}>Secure Video Player</Text>
            <Text style={styles.videoPlaceholderNote}>Video plays via expo-av (Expo Go compatible)</Text>
          </View>

          {/* Controls Overlay */}
          {showControls && (
            <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }} style={styles.controlsOverlay}>
              {/* Top bar */}
              <View style={styles.topBar}>
                <TouchableOpacity onPress={goBack}>
                  <ChevronLeft size={24} color="#fff" />
                </TouchableOpacity>
                <Text style={styles.videoTitle} numberOfLines={1}>Lesson 1: Introduction</Text>
                <TouchableOpacity onPress={() => setIsFullscreen(!isFullscreen)}>
                  {isFullscreen ? <Minimize size={20} color="#fff" /> : <Maximize size={20} color="#fff" />}
                </TouchableOpacity>
              </View>

              {/* Center controls */}
              <View style={styles.centerControls}>
                <TouchableOpacity onPress={() => {}} style={styles.controlBtn}>
                  <SkipBack size={24} color="#fff" />
                </TouchableOpacity>
                <TouchableOpacity style={styles.playBtn} onPress={() => setIsPlaying(!isPlaying)}>
                  {isPlaying ? <Pause size={32} color="#fff" fill="#fff" /> : <Play size={32} color="#fff" fill="#fff" />}
                </TouchableOpacity>
                <TouchableOpacity onPress={() => {}} style={styles.controlBtn}>
                  <SkipForward size={24} color="#fff" />
                </TouchableOpacity>
              </View>

              {/* Bottom bar */}
              <View style={styles.bottomBar}>
                <Text style={styles.timeText}>{formatTime(currentTime)}</Text>
                <View style={styles.progressBarContainer}>
                  <View style={styles.progressBarBackground}>
                    <View style={[styles.progressBarFill, { width: `${progress * 100}%` }]} />
                  </View>
                </View>
                <Text style={styles.timeText}>{formatTime(duration)}</Text>
                <TouchableOpacity onPress={() => {
                  const nextSpeed = speeds[(speeds.indexOf(speed) + 1) % speeds.length];
                  setSpeed(nextSpeed);
                }}>
                  <Text style={styles.speedText}>{speed}x</Text>
                </TouchableOpacity>
              </View>
            </MotiView>
          )}
        </TouchableOpacity>

        {/* Video info */}
        <View style={[styles.videoInfo, { backgroundColor: colors.background }]}>
          <Text style={[styles.videoInfoTitle, { color: colors.foreground }]}>Introduction to Computer Science</Text>
          <Text style={[styles.videoInfoChapter, { color: colors.mutedForeground }]}>Chapter 1 · Lesson 1 of 8</Text>

          {/* Quick Actions */}
          <View style={styles.quickActions}>
            <TouchableOpacity style={[styles.quickAction, { backgroundColor: `${colors.primary}15` }]}>
              <RotateCcw size={16} color={colors.primary} />
              <Text style={[styles.quickActionText, { color: colors.primary }]}>Restart</Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.quickAction, { backgroundColor: `${colors.emeraldAccent}15` }]}>
              <Subtitles size={16} color={colors.emeraldAccent} />
              <Text style={[styles.quickActionText, { color: colors.emeraldAccent }]}>Subtitles</Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.quickAction, { backgroundColor: `${colors.amberAccent}15` }]}>
              <Settings size={16} color={colors.amberAccent} />
              <Text style={[styles.quickActionText, { color: colors.amberAccent }]}>Quality</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#000' },
  container: { flex: 1 },
  videoArea: { width, height: width * 9 / 16, backgroundColor: '#0c1222', position: 'relative' },
  videoPlaceholder: { flex: 1, alignItems: 'center', justifyContent: 'center' },
  videoPlaceholderText: { fontSize: 32, fontWeight: '800', color: '#0ea5e9' },
  videoPlaceholderSub: { fontSize: 14, color: '#94a3b8', marginTop: 4 },
  videoPlaceholderNote: { fontSize: 10, color: '#64748b', marginTop: 8 },
  controlsOverlay: { position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'space-between' },
  topBar: { flexDirection: 'row', alignItems: 'center', gap: 12, padding: 12 },
  videoTitle: { flex: 1, color: '#fff', fontSize: 14, fontWeight: '600' },
  centerControls: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 40 },
  controlBtn: { padding: 8 },
  playBtn: { width: 64, height: 64, borderRadius: 32, backgroundColor: 'rgba(14,165,233,0.3)', alignItems: 'center', justifyContent: 'center' },
  bottomBar: { flexDirection: 'row', alignItems: 'center', gap: 8, padding: 12 },
  timeText: { color: '#fff', fontSize: 11, fontWeight: '500', minWidth: 36 },
  progressBarContainer: { flex: 1, height: 20, justifyContent: 'center' },
  progressBarBackground: { height: 4, borderRadius: 2, backgroundColor: 'rgba(255,255,255,0.2)' },
  progressBarFill: { height: '100%', borderRadius: 2, backgroundColor: '#0ea5e9' },
  speedText: { color: '#0ea5e9', fontSize: 12, fontWeight: '700' },
  videoInfo: { flex: 1, padding: 16 },
  videoInfoTitle: { fontSize: 16, fontWeight: '700', marginBottom: 4 },
  videoInfoChapter: { fontSize: 13, marginBottom: 16 },
  quickActions: { flexDirection: 'row', gap: 12, marginTop: 8 },
  quickAction: { flexDirection: 'row', alignItems: 'center', gap: 6, paddingHorizontal: 14, paddingVertical: 8, borderRadius: 20 },
  quickActionText: { fontSize: 12, fontWeight: '600' },
});
