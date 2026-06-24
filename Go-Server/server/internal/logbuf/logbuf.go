package logbuf

import (
	"bytes"
	"io"
	"sync"
)

const maxLines = 500

type Buffer struct {
	mu    sync.RWMutex
	lines []string
	seq   int
}

func (b *Buffer) Write(p []byte) (n int, err error) {
	b.mu.Lock()
	defer b.mu.Unlock()
	for _, line := range bytes.Split(p, []byte("\n")) {
		s := string(bytes.TrimRight(line, "\r"))
		if s == "" {
			continue
		}
		// Scrub secrets before retaining the line — this buffer is served verbatim
		// over HTTP at /admin/server-logs.
		s = redactSecrets(s)
		if len(b.lines) >= maxLines {
			b.lines = b.lines[1:]
		}
		b.lines = append(b.lines, s)
		b.seq++
	}
	return len(p), nil
}

func (b *Buffer) Lines(after int) (lines []string, seq int) {
	b.mu.RLock()
	defer b.mu.RUnlock()
	start := b.seq - len(b.lines)
	if after >= b.seq {
		return nil, b.seq
	}
	idx := after - start
	if idx < 0 {
		idx = 0
	}
	return append([]string{}, b.lines[idx:]...), b.seq
}

var Global = &Buffer{}

func Writer() io.Writer { return Global }
