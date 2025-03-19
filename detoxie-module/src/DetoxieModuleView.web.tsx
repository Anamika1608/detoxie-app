import * as React from 'react';

import { DetoxieModuleViewProps } from './DetoxieModule.types';

export default function DetoxieModuleView(props: DetoxieModuleViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
