export type ReelsTimeStats = {
    sessionTime: number; // Current session time in milliseconds
    totalTime: number; // Total time in milliseconds
};

export type InstagramMonitorEvents = {
    reelsSessionStarted: void;
    reelsSessionEnded: ReelsTimeStats;
    reelsTimeUpdate: ReelsTimeStats;
};

export interface InstagramMonitorModule {
    openAccessibilitySettings(): void;
    isAccessibilityServiceEnabled(): Promise<boolean>;
    getCurrentStats(): Promise<ReelsTimeStats>;
}