import { Pipe, PipeTransform } from '@angular/core';

import dayjs from 'dayjs/esm';

@Pipe({
  standalone: true,
  name: 'formatMediumDate',
})
export default class FormatMediumDatePipe implements PipeTransform {
  transform(day: dayjs.Dayjs | null | undefined): string {
    console.log('***' + day);
    console.log('***' + typeof day);
    return day ? day.format('D MMM YYYY') : '';
  }
}
