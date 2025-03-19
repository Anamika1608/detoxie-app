import { requireNativeView } from 'expo';
import * as React from 'react';

import { DetoxieModuleViewProps } from './DetoxieModule.types';

const NativeView: React.ComponentType<DetoxieModuleViewProps> =
  requireNativeView('DetoxieModule');

export default function DetoxieModuleView(props: DetoxieModuleViewProps) {
  return <NativeView {...props} />;
}
