function signal_sync = syncsignal(signal, y)
    
    [a, ~] = xcorr(signal, y);
    a = a(ceil(length(a) / 2) : end);
    [peaks, loc] = findpeaks(a);
    maxpeaks = max(peaks);
    peaks(peaks < 0.7 * maxpeaks) = 0;

    status = 1;
    for ii = 1 : length(peaks)
        switch status
            case 1
                if peaks(ii) ~= 0
                    startpoint = loc(ii);
                    status = 2;
                end
            case 2
                if peaks(ii) == 0
                    endpoint = loc(ii - 1);
                    break;
                end 
            otherwise
                break;
        end
    end
    signal_sync = signal(startpoint : endpoint - 1);
    
end